# ERP SaaS Multi-Tenant - Gestión de Eventos y Catering

Este es un sistema ERP modular diseñado bajo el modelo de Software as a Service (SaaS). Implementa una arquitectura distribuida para gestionar múltiples clientes (Tenants) con aislamiento total de datos.

## 🛠️ Pilares de la Arquitectura

- **Multi-tenancy Físico:** Estrategia de base de datos por inquilino. Cada cliente tiene su propia instancia de base de datos PostgreSQL, gestionada mediante ruteo dinámico en tiempo de ejecución.
- **Arquitectura Dirigida por Eventos (EDA):** Comunicación desacoplada entre microservicios mediante **Apache Kafka**.
- **Consistencia Eventual (Transactional Outbox):** Garantía de que los eventos de ventas se procesen en operaciones sin pérdida de datos, incluso ante fallos de red.
- **Seguridad Contextual:** Uso de tokens JWT para propagar el `tenantId` a través de todos los servicios.

## 📦 Estructura del Proyecto

* `erp-common`: Módulo central que contiene la lógica de ruteo dinámico, configuración de JPA multi-tenant y migraciones con Flyway.
* `ms-auth`: Servicio de identidad, aprovisionamiento de bases de datos para nuevos inquilinos y autenticación.
* `ms-sales`: Gestión de presupuestos, reservas y espacios. Emisor de eventos de negocio.
* `ms-operations`: Gestión logística. Consume eventos de ventas para generar hojas de servicio (Function Sheets) de forma automática.

## 🚀 Tecnologías

- Java 17 / Spring Boot 3.2.4
- Spring Data JPA / Hibernate
- PostgreSQL (Base central + Bases de inquilinos)
- Apache Kafka (KRaft mode)
- Flyway (Migraciones de base de datos distribuidas)
- Maven (Gestión de módulos)