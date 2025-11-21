# app/main.py
from fastapi import FastAPI, status

from .schemas import (
    Item,
    CalcResult,
    ApiResponse,
    InvoiceItemsRequest
)
from .services import calculate_invoice
from .error_handler import configure_exception_handlers


# Crear la aplicación FastAPI
app = FastAPI(
    title="Billing Calculator MS",
    description="Microservicio de cálculo de facturas para CASTOR Billing",
    version="1.0.0",
)

# Configurar manejadores globales de errores
configure_exception_handlers(app)


@app.post("/calculate", response_model=ApiResponse[CalcResult])
def calculate(request: InvoiceItemsRequest):
    """
    Endpoint principal para calcular subtotal, impuestos, descuentos y total.
    """
    result: CalcResult = calculate_invoice(request.items)

    response = ApiResponse[CalcResult](
        success=True,
        message="Cálculo realizado correctamente",
        data=result,
        status=status.HTTP_200_OK
    )

    return response
