# app/exceptions.py

class BusinessException(Exception):
    # Excepción de negocio con código de estado HTTP
    def __init__(self, message: str, status_code: int = 400):
        # Mensaje legible para el usuario
        self.message = message
        # Código HTTP asociado
        self.status_code = status_code
        super().__init__(message)
