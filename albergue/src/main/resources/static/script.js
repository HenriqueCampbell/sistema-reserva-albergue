const clientForm = document.querySelector("#client-form");
const availabilityForm = document.querySelector("#availability-form");
const bookingForm = document.querySelector("#booking-form");
const bedsContainer = document.querySelector("#beds");
const selectedBedsContainer = document.querySelector("#selected-beds");
const selectedBeds = new Map();
const reservationTypeSelect = document.querySelector("#reservation-type");
let availableBeds = [];
let searchedDates = null;

clientForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(clientForm);
    const client = Object.fromEntries(formData.entries());
    showMessage("client-message", "Cadastrando cliente...");
    try {
        const createdClient = await request("/api/clientes", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(client)
        });
        bookingForm.elements.clienteId.value = createdClient.id;
        clientForm.reset();
        showMessage("client-message", `Cliente cadastrado com sucesso. ID ${createdClient.id} preenchido na reserva.`);
    } catch (error) {
        showMessage("client-message", `Não foi possível cadastrar o cliente: ${error.message}`, true);
    }
});

function showMessage(elementId, text, isError = false) {
    const element = document.getElementById(elementId);
    element.textContent = text;
    element.className = `message ${isError ? "error" : "success"}`;
}

async function request(url, options = {}) {
    const response = await fetch(url, options);
    const text = await response.text();
    let body = null;
    if (text) {
        try {
            body = JSON.parse(text);
        } catch {
            if (response.ok) {
                throw new Error("A API retornou uma resposta JSON inválida.");
            }
            body = text;
        }
    }
    if (!response.ok) {
        const detail = typeof body === "string"
            ? body
            : body?.message || body?.detail || body?.error || `Erro HTTP ${response.status}`;
        throw new Error(detail);
    }
    return body;
}

function formatCurrency(value) {
    const amount = Number(value);
    return Number.isFinite(amount)
        ? amount.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
        : "não informado";
}

function describeBed(bed) {
    const room = bed.quarto || {};
    const details = [
        `Vaga ${bed.id ?? "sem ID"}`,
        bed.descricao || "Características da vaga não informadas",
        `Quarto ${room.numero ?? "não informado"}`,
        room.capacidade != null ? `Capacidade: ${room.capacidade}` : null,
        room.banheiro ? "Com banheiro" : "Sem banheiro",
        `Diária: ${formatCurrency(bed.valor)}`
    ].filter(Boolean);
    return details.join(" · ");
}

function addSelectableItem(container, id, description, selectionId = id) {
    const card = document.createElement("div");
    card.className = "result";
    const label = document.createElement("label");
    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.value = id;
    checkbox.addEventListener("change", () => {
        if (checkbox.checked) selectedBeds.set(String(selectionId), description);
        else selectedBeds.delete(String(selectionId));
        renderSelectedBeds();
    });
    label.append(checkbox, document.createTextNode(description));
    card.append(label);
    container.append(card);
}

function renderAvailableItems() {
    bedsContainer.replaceChildren();
    selectedBeds.clear();
    renderSelectedBeds();
    if (reservationTypeSelect.value === "VAGA") {
        showMessage("availability-message", `${availableBeds.length} vaga(s) encontrada(s). Selecione as que deseja reservar.`);
        for (const bed of availableBeds) {
            addSelectableItem(bedsContainer, bed.id, describeBed(bed));
        }
        return;
    }

    const rooms = new Map();
    for (const bed of availableBeds) {
        const room = bed.quarto;
        if (!room?.id) continue;
        if (!rooms.has(room.id)) rooms.set(room.id, { room, beds: [] });
        rooms.get(room.id).beds.push(bed);
    }

    const fullyAvailableRooms = Array.from(rooms.values())
        .filter(({ room, beds }) => room.capacidade && beds.length === room.capacidade);
    if (fullyAvailableRooms.length === 0) {
        showMessage("availability-message", "Há vagas individuais disponíveis, mas nenhum quarto está com todas as vagas livres para o período.");
        return;
    }

    showMessage("availability-message", `${fullyAvailableRooms.length} quarto(s) inteiro(s) disponível(is).`);
    for (const { room, beds } of fullyAvailableRooms) {
        const totalDailyPrice = beds.reduce((total, bed) => total + Number(bed.valor), 0);
        const description = [
            `Quarto ${room.numero}`,
            `Capacidade: ${room.capacidade}`,
            room.banheiro ? "Com banheiro" : "Sem banheiro",
            `Todas as ${beds.length} vagas livres`,
            `Diária do quarto: ${formatCurrency(totalDailyPrice)}`
        ].join(" · ");
        addSelectableItem(bedsContainer, room.id, description);
    }
}

function renderSelectedBeds() {
    selectedBedsContainer.replaceChildren();
    if (selectedBeds.size === 0) {
        selectedBedsContainer.textContent = "Busque a disponibilidade e selecione vagas ou quartos.";
        return;
    }
    for (const [id, label] of selectedBeds) {
        const item = document.createElement("div");
        item.textContent = label;
        selectedBedsContainer.append(item);
    }
}

availabilityForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = new FormData(availabilityForm);
    const dataInicio = data.get("dataInicio");
    const dataFim = data.get("dataFim");
    if (dataFim <= dataInicio) {
        showMessage("availability-message", "A data de fim deve ser posterior à data de início.", true);
        return;
    }

    showMessage("availability-message", "Buscando vagas...");
    bedsContainer.replaceChildren();
    selectedBeds.clear();
    availableBeds = [];
    searchedDates = null;
    renderSelectedBeds();
    try {
        const beds = await request(`/api/quartos/disponiveis?dataInicio=${encodeURIComponent(dataInicio)}&dataFim=${encodeURIComponent(dataFim)}`);
        searchedDates = { dataEntrada: dataInicio, dataSaida: dataFim };
        if (!Array.isArray(beds) || beds.length === 0) {
            showMessage("availability-message", "Nenhuma vaga disponível para o período.");
            return;
        }
        availableBeds = beds;
        renderAvailableItems();
    } catch (error) {
        searchedDates = null;
        showMessage("availability-message", `Não foi possível consultar a disponibilidade: ${error.message}`, true);
    }
});

reservationTypeSelect.addEventListener("change", () => {
    if (searchedDates && availableBeds.length > 0) renderAvailableItems();
});

bookingForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    if (!searchedDates || selectedBeds.size === 0) {
        showMessage("booking-message", "Consulte a disponibilidade e selecione pelo menos uma vaga.", true);
        return;
    }

    const formData = new FormData(bookingForm);
    const requestBody = {
        clienteId: Number(formData.get("clienteId")),
        ...searchedDates,
        tipoReserva: reservationTypeSelect.value,
        itensIds: Array.from(selectedBeds.keys(), Number)
    };
    for (const field of ["cartao", "validade", "cvv"]) {
        bookingForm.elements[field].value = "";
    }
    showMessage("booking-message", "Criando reserva e simulando o pagamento...");
    try {
        let reservation = await request("/api/reservas", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestBody)
        });
        if (!reservation?.id) {
            throw new Error("A API não retornou o ID da reserva criada.");
        }
        try {
            reservation = await request(`/api/reservas/${reservation.id}/confirmar-pagamento-simulado`, { method: "POST" });
            if (reservation?.status !== "CONFIRMADA") {
                throw new Error(`A API retornou o status ${reservation?.status ?? "não informado"}.`);
            }
        } catch (error) {
            showMessage("booking-message", `A reserva ${reservation.id} foi criada, mas permaneceu pendente porque a confirmação simulada falhou: ${error.message}. Você pode tentar confirmar pela lista de reservas.`, true);
            await loadReservations("/api/reservas");
            return;
        }
        showMessage("booking-message", `Reserva ${reservation.id} confirmada. Pagamento fictício aprovado localmente; nenhum dado de cartão foi enviado.`);
        await loadReservations("/api/reservas");
    } catch (error) {
        showMessage("booking-message", `Não foi possível criar a reserva: ${error.message}`, true);
    }
});

async function loadReservations(url) {
    showMessage("reservations-message", "Carregando reservas...");
    const container = document.querySelector("#reservations");
    container.replaceChildren();
    try {
        const reservations = await request(url);
        if (!Array.isArray(reservations) || reservations.length === 0) {
            showMessage("reservations-message", "Nenhuma reserva encontrada.");
            return;
        }
        showMessage("reservations-message", `${reservations.length} reserva(s) encontrada(s).`);
        for (const reservation of reservations) {
            const card = document.createElement("div");
            card.className = "result";
            const summary = document.createElement("p");
            summary.textContent = `Reserva ${reservation.id} · ${reservation.tipoReserva === "QUARTO" ? "Quarto inteiro" : "Vaga(s)"} · Cliente ${reservation.cliente?.id ?? "não informado"} · ${reservation.status ?? "status não informado"} · Entrada: ${reservation.dataEntrada ?? "—"} · Saída: ${reservation.dataSaida ?? "—"} · Total: ${formatCurrency(reservation.valorTotal)}`;
            if (reservation.status === "PENDENTE") {
                const confirmButton = document.createElement("button");
                confirmButton.type = "button";
                confirmButton.textContent = "Simular pagamento e confirmar";
                confirmButton.addEventListener("click", async () => {
                    try {
                        await request(`/api/reservas/${reservation.id}/confirmar-pagamento-simulado`, { method: "POST" });
                        await loadReservations(url);
                    } catch (error) {
                        showMessage("reservations-message", `Não foi possível confirmar a reserva: ${error.message}`, true);
                    }
                });
                card.append(summary, confirmButton);
            } else {
                card.append(summary);
            }
            const cancelButton = document.createElement("button");
            cancelButton.type = "button";
            cancelButton.className = "danger";
            cancelButton.textContent = "Cancelar reserva";
            cancelButton.addEventListener("click", async () => {
                if (!window.confirm(`Deseja cancelar a reserva ${reservation.id}?`)) return;
                try {
                    await request(`/api/reservas/${reservation.id}`, { method: "DELETE" });
                    await loadReservations(url);
                } catch (error) {
                    showMessage("reservations-message", `Não foi possível cancelar a reserva: ${error.message}`, true);
                }
            });
            card.append(cancelButton);
            container.append(card);
        }
    } catch (error) {
        showMessage("reservations-message", `Não foi possível consultar reservas: ${error.message}`, true);
    }
}

document.querySelector("#list-reservations").addEventListener("click", () => loadReservations("/api/reservas"));
document.querySelector("#list-client-reservations").addEventListener("click", () => {
    const clientId = document.querySelector("#reservation-client-id").value;
    if (!clientId) {
        showMessage("reservations-message", "Informe o ID do cliente.", true);
        return;
    }
    loadReservations(`/api/reservas/cliente/${encodeURIComponent(clientId)}`);
});