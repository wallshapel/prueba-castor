# app/schemas.py
from typing import Generic, Optional, TypeVar, List
from pydantic import BaseModel, Field
from pydantic.generics import GenericModel

# Tipo genérico para el campo "data"
T = TypeVar("T")


class Item(BaseModel):
    product: str = Field(..., min_length=1, description="Nombre del producto")
    price: float = Field(..., ge=0, description="Precio unitario")
    quantity: int = Field(..., ge=1, description="Cantidad")


class InvoiceItemsRequest(BaseModel):
    items: List[Item]


class CalcResult(BaseModel):
    subtotal: float
    tax: float
    discount: float
    total: float


class ApiResponse(GenericModel, Generic[T]):
    success: bool
    message: str
    data: Optional[T] = None
    status: int
