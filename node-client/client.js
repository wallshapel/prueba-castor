// client.js
// Cliente interactivo para consumir los endpoints del backend Java
// Ejecutar con: node client.js

import axios from "axios";
import readline from "readline";

const BASE_URL = "http://localhost:8081";

// Interface para leer entrada del usuario
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

// Función para hacer preguntas al usuario
function askQuestion(question) {
  return new Promise((resolve) => {
    rl.question(question, resolve);
  });
}

// ==========================
//   CLIENTES
// ==========================

async function listClients() {
  try {
    const res = await axios.get(`${BASE_URL}/clientes`);
    console.log("\n=== LISTA DE CLIENTES ===");
    console.log(JSON.stringify(res.data, null, 2));
  } catch (err) {
    console.error("Error listando clientes:", err.response?.data || err.message);
  }
}

async function createClient() {
  try {
    const name = await askQuestion("Nombre del cliente: ");
    const email = await askQuestion("Email del cliente: ");
    const active = await askQuestion("¿Activo? (true/false): ");
    
    const body = {
      name: name,
      email: email,
      active: active.toLowerCase() === 'true'
    };

    const res = await axios.post(`${BASE_URL}/clientes`, body);
    console.log("\n=== CLIENTE CREADO ===");
    console.log(JSON.stringify(res.data, null, 2));
  } catch (err) {
    console.error("Error creando cliente:", err.response?.data || err.message);
  }
}

async function getClientById() {
  try {
    const id = await askQuestion("ID del cliente a buscar: ");
    const res = await axios.get(`${BASE_URL}/clientes/${id}`);
    console.log("\n=== CLIENTE POR ID ===");
    console.log(JSON.stringify(res.data, null, 2));
  } catch (err) {
    console.error("Error obteniendo cliente:", err.response?.data || err.message);
  }
}

async function updateClient() {
  try {
    const id = await askQuestion("ID del cliente a actualizar: ");
    const name = await askQuestion("Nuevo nombre: ");
    const email = await askQuestion("Nuevo email: ");
    const active = await askQuestion("¿Activo? (true/false): ");
    
    const body = {
      name: name,
      email: email,
      active: active.toLowerCase() === 'true'
    };

    const res = await axios.put(`${BASE_URL}/clientes/${id}`, body);
    console.log("\n=== CLIENTE ACTUALIZADO ===");
    console.log(JSON.stringify(res.data, null, 2));
  } catch (err) {
    console.error("Error actualizando cliente:", err.response?.data || err.message);
  }
}

async function deleteClient() {
  try {
    const id = await askQuestion("ID del cliente a eliminar: ");
    const res = await axios.delete(`${BASE_URL}/clientes/${id}`);
    console.log("\n=== CLIENTE ELIMINADO ===");
    console.log(JSON.stringify(res.data, null, 2));
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
    console.log(JSON.stringify(res.data, null, 2));
  } catch (err) {
    console.error("Error listando facturas:", err.response?.data || err.message);
  }
}

async function createInvoice() {
  try {
    const customerId = await askQuestion("ID del cliente para la factura: ");
    const product = await askQuestion("Producto: ");
    const price = await askQuestion("Precio: ");
    const quantity = await askQuestion("Cantidad: ");
    
    const body = {
      customerId: customerId,
      items: [
        {
          product: product,
          price: parseFloat(price),
          quantity: parseInt(quantity)
        }
      ]
    };

    const res = await axios.post(`${BASE_URL}/facturas`, body);
    console.log("\n=== FACTURA CREADA ===");
    console.log(JSON.stringify(res.data, null, 2));
  } catch (err) {
    console.error("Error creando factura:", err.response?.data || err.message);
  }
}

async function getInvoicesByCustomer() {
  try {
    const customerId = await askQuestion("ID del cliente para buscar facturas: ");
    const res = await axios.get(`${BASE_URL}/facturas/cliente/${customerId}`);
    console.log("\n=== FACTURAS POR CLIENTE ===");
    console.log(JSON.stringify(res.data, null, 2));
  } catch (err) {
    console.error(
      "Error obteniendo facturas por cliente:",
      err.response?.data || err.message
    );
  }
}

// ==========================
//   MENÚ INTERACTIVO
// ==========================

function showMenu() {
  console.log("\n" + "=".repeat(50));
  console.log("🎯 CLIENTE INTERACTIVO API JAVA");
  console.log("=".repeat(50));
  console.log("📋 CLIENTES:");
  console.log("1.  Listar todos los clientes");
  console.log("2.  Crear nuevo cliente");
  console.log("3.  Obtener cliente por ID");
  console.log("4.  Actualizar cliente");
  console.log("5.  Eliminar cliente");
  console.log("\n🧾 FACTURAS:");
  console.log("6.  Listar todas las facturas");
  console.log("7.  Crear nueva factura");
  console.log("8.  Obtener facturas por cliente");
  console.log("\n❌ SALIR:");
  console.log("0.  Terminar programa");
  console.log("=".repeat(50));
}

async function handleChoice(choice) {
  switch (choice) {
    case '1':
      await listClients();
      break;
    case '2':
      await createClient();
      break;
    case '3':
      await getClientById();
      break;
    case '4':
      await updateClient();
      break;
    case '5':
      await deleteClient();
      break;
    case '6':
      await listInvoices();
      break;
    case '7':
      await createInvoice();
      break;
    case '8':
      await getInvoicesByCustomer();
      break;
    case '0':
      console.log("👋 ¡Hasta luego!");
      rl.close();
      return true; // Indicar que debe terminar
    default:
      console.log("❌ Opción no válida. Por favor elige una opción del menú.");
  }
  return false; // Indicar que debe continuar
}

async function main() {
  console.log("🚀 Cliente Node.js interactivo inicializado!");
  console.log("🌐 Conectando a:", BASE_URL);

  let shouldExit = false;
  
  while (!shouldExit) {
    showMenu();
    const choice = await askQuestion("\n👉 Elige una opción (0-8): ");
    shouldExit = await handleChoice(choice);
    
    if (!shouldExit) {
      await askQuestion("\n📝 Presiona Enter para continuar...");
    }
  }
}

// Manejar cierre graceful
rl.on('close', () => {
  console.log("\n✨ Programa terminado.");
  process.exit(0);
});

main();