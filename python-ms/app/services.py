# app/services.py
from typing import List

from .schemas import Item, CalcResult
from .exceptions import BusinessException


# Por ahora definimos tasa de impuesto y reglas de descuento simples
TAX_RATE = 0.19


def calculate_invoice(items: List[Item]) -> CalcResult:
    # Validar que haya al menos un ítem
    if not items:
        raise BusinessException("La factura debe contener al menos un ítem", status_code=400)

    # Calcular subtotal sumando precio * cantidad de cada ítem
    subtotal = sum(i.price * i.quantity for i in items)

    # Validar que el subtotal no sea cero
    if subtotal <= 0:
        raise BusinessException("El subtotal calculado debe ser mayor que cero", status_code=400)

    # Calcular impuestos
    tax = subtotal * TAX_RATE

    # Regla de ejemplo para descuento:
    # si el subtotal es mayor a 1_000_000 aplicamos 5% de descuento
    discount = 0.0
    if subtotal > 1_000_000:
        discount = subtotal * 0.05

    # Calcular total final
    total = subtotal + tax - discount

    # Redondear resultados a 2 decimales
    return CalcResult(
        subtotal=round(subtotal, 2),
        tax=round(tax, 2),
        discount=round(discount, 2),
        total=round(total, 2),
    )
