# app/error_handler.py
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from starlette.status import HTTP_422_UNPROCESSABLE_ENTITY

from .exceptions import BusinessException
from .schemas import ApiResponse


def configure_exception_handlers(app: FastAPI) -> None:
    # Manejador para errores de negocio
    @app.exception_handler(BusinessException)
    async def business_exception_handler(request: Request, exc: BusinessException):
        # Respuesta estandarizada para BusinessException
        body = ApiResponse[None](
            success=False,
            message=exc.message,
            data=None,
            status=exc.status_code,
        )
        return JSONResponse(
            status_code=exc.status_code,
            content=body.model_dump(),
        )

    # Manejador para errores de validación de FastAPI/Pydantic
    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(request: Request, exc: RequestValidationError):
        # Aquí podríamos mapear los errores a un mensaje más amigable
        # Por simplicidad devolvemos los detalles crudos en "data"
        body = ApiResponse(
            success=False,
            message="Error de validación en los datos de entrada",
            data=exc.errors(),
            status=HTTP_422_UNPROCESSABLE_ENTITY,
        )
        return JSONResponse(
            status_code=HTTP_422_UNPROCESSABLE_ENTITY,
            content=body.model_dump(),
        )
