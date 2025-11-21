// client.js
// Cliente de prueba para consumir los endpoints del backend Java
// Ejecutar con: node client.js

import axios from "axios";

const BASE_URL = "http://localhost:8081";

// ==========================
//   CLIENTES
// ==========================

async function listClients() {
  try {
    const res = await axios.get(`${BASE_URL}/clientes`);
    console.log("\n=== LISTA DE CLIENTES ===");
    console.log(res.data);
  } catch (err) {
    console.error("Error listando clientes:", err.response?.data || err.message);
  }
}

async function createClient() {
  try {
    const body = {
      name: "Legato desde Node",
      email: "node_client@example.com",
      active: true
    };

    const res = await axios.post(`${BASE_URL}/clientes`, body);
    console.log("\n=== CLIENTE CREADO ===");
    console.log(res.data);

    return res.data.data.id; // retorno del ID creado
  } catch (err) {
    console.error("Error creando cliente:", err.response?.data || err.message);
  }
}

async function getClientById(id) {
  try {
    const res = await axios.get(`${BASE_URL}/clientes/${id}`);
    console.log("\n=== CLIENTE POR ID ===");
    console.log(res.data);
  } catch (err) {
    console.error("Error obteniendo cliente:", err.response?.data || err.message);
  }
}

async function updateClient(id) {
  try {
    const body = {
      name: "Legato Actualizado desde Node",
      email: "node_updated@example.com",
      active: true
    };

    const res = await axios.put(`${BASE_URL}/clientes/${id}`, body);
    console.log("\n=== CLIENTE ACTUALIZADO ===");
    console.log(res.data);
  } catch (err) {
    console.error("Error actualizando cliente:", err.response?.data || err.message);
  }
}

async function deleteClient(id) {
  try {
    const res = await axios.delete(`${BASE_URL}/clientes/${id}`);
    console.log("\n=== CLIENTE ELIMINADO ===");
    console.log(res.data);
  } catch (err) {
    console.error("Error eliminando cliente:", err.response?.data || err.message);
  }
}

// ==========================
//   FACTURAS
// ==========================

async function listInvoices() {
  try {
    const res = await axios.get(`${BASE_URL}/facturas`);
    console.log("\n=== LISTA DE FACTURAS ===");
    console.log(res.data);
  } catch (err) {
    console.error("Error listando facturas:", err.response?.data || err.message);
  }
}

async function createInvoice(customerId) {
  try {
    const body = {
      customerId: customerId,
      items: [
        {
          product: "Producto desde Node",
          price: 50,
          quantity: 2
        }
      ]
    };

    const res = await axios.post(`${BASE_URL}/facturas`, body);
    console.log("\n=== FACTURA CREADA ===");
    console.log(res.data);

    return res.data.data.id; // retorno del ID de factura creada
  } catch (err) {
    console.error("Error creando factura:", err.response?.data || err.message);
  }
}

async function getInvoicesByCustomer(customerId) {
  try {
    const res = await axios.get(`${BASE_URL}/facturas/cliente/${customerId}`);
    console.log("\n=== FACTURAS POR CLIENTE ===");
    console.log(res.data);
  } catch (err) {
    console.error(
      "Error obteniendo facturas por cliente:",
      err.response?.data || err.message
    );
  }
}


// ==========================
//   EJECUCIÓN PRINCIPAL
// ==========================

async function main() {
  console.log("Cliente Node.js inicializado. Probando API Java...\n");

  // CLIENTES
  await listClients();
  const newCustomerId = await createClient();

  if (newCustomerId) {
    await getClientById(newCustomerId);
    await updateClient(newCustomerId);

    // FACTURAS
    await listInvoices();
    const invoiceId = await createInvoice(newCustomerId);
    await getInvoicesByCustomer(newCustomerId);

    // DELETE CLIENT
    await deleteClient(newCustomerId);
  }
}


main();
