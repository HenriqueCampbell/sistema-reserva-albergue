const clientForm = document.querySelector("#client-form");
const availabilityForm = document.querySelector("#availability-form");
const bookingForm = document.querySelector("#booking-form");
const bedsContainer = document.querySelector("#beds");
const selectedBedsContainer = document.querySelector("#selected-beds");
const selectedBeds = new Map();
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
            body = text;
        }
    }
    if (!response.ok) {
        const detail = typeof body === "string" ? body : JSON.stringify(body);
        throw new Error(detail || `Erro HTTP ${response.status}`);
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
        room.descricao || "Características do quarto não informadas",
        room.capacidade != null ? `Capacidade: ${room.capacidade}` : null,
        `Diária: ${formatCurrency(bed.valor)}`
    ].filter(Boolean);
    return details.join(" · ");
}

function renderSelectedBeds() {
    selectedBedsContainer.replaceChildren();
    if (selectedBeds.size === 0) {
        selectedBedsContainer.textContent = "Busque a disponibilidade e selecione uma ou mais vagas.";
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
    renderSelectedBeds();
    try {
        const beds = await request(`/api/quartos/disponiveis?dataInicio=${encodeURIComponent(dataInicio)}&dataFim=${encodeURIComponent(dataFim)}`);
        searchedDates = { dataEntrada: dataInicio, dataSaida: dataFim };
        if (!Array.isArray(beds) || beds.length === 0) {
            showMessage("availability-message", "Nenhuma vaga disponível para o período.");
            return;
        }

        showMessage("availability-message", `${beds.length} vaga(s) encontrada(s). Selecione as que deseja reservar.`);
        for (const bed of beds) {
            const card = document.createElement("div");
            card.className = "result";
            const label = document.createElement("label");
            const checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.value = bed.id;
            checkbox.addEventListener("change", () => {
                if (checkbox.checked) selectedBeds.set(String(bed.id), describeBed(bed));
                else selectedBeds.delete(String(bed.id));
                renderSelectedBeds();
            });
            label.append(checkbox, document.createTextNode(describeBed(bed)));
            card.append(label);
            bedsContainer.append(card);
        }
    } catch (error) {
        searchedDates = null;
        showMessage("availability-message", `Não foi possível consultar a disponibilidade: ${error.message}`, true);
    }
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
        leitosIds: Array.from(selectedBeds.keys(), Number)
    };
    const paymentDemoProvided = Boolean(formData.get("cartao") || formData.get("validade") || formData.get("cvv"));
    for (const field of ["cartao", "validade", "cvv"]) {
        bookingForm.elements[field].value = "";
    }
    showMessage("booking-message", "Enviando reserva... O pagamento é apenas demonstrativo.");
    try {
        const reservation = await request("/api/reservas", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestBody)
        });
        showMessage("booking-message", `Reserva criada. ID: ${reservation?.id ?? "não informado"} · Status: ${reservation?.status ?? "não informado"} · Pagamento: ${paymentDemoProvided ? "simulação local (não enviada)" : "não informado"}. Nenhum dado de cartão foi enviado.`);
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
            summary.textContent = `Reserva ${reservation.id} · Cliente ${reservation.cliente?.id ?? "não informado"} · ${reservation.status ?? "status não informado"} · Entrada: ${reservation.dataEntrada ?? "—"} · Saída: ${reservation.dataSaida ?? "—"} · Total: ${formatCurrency(reservation.valorTotal)}`;
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
            card.append(summary, cancelButton);
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