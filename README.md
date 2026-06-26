# ERP SaaS Multi-Tenant - Gestión Integral de Hostelería y Eventos

Este es un sistema ERP modular y distribuido diseñado bajo el modelo de Software as a Service (SaaS). Está especializado en la gestión de hostelería, catering y eventos, implementando una arquitectura de microservicios con aislamiento total de datos por cliente (Tenant) y comunicación asíncrona robusta.

## 🛠️ Pilares de la Arquitectura

* **Multi-tenancy Físico (Database-per-Tenant):** Cada cliente posee su propia base de datos PostgreSQL aislada. El sistema utiliza ruteo dinámico de conexiones (`DynamicRoutingDataSource`) en tiempo de ejecución interceptando el token JWT.
* **Arquitectura Dirigida por Eventos (EDA):** Comunicación totalmente desacoplada entre microservicios mediante Apache Kafka.
* **Consistencia Eventual (Transactional Outbox):** Garantiza que ningún evento de dominio se pierda ante caídas de red o de Kafka. Las transacciones de base de datos y la publicación de eventos son atómicas.
* **Replicación de Datos y CQRS:** Los microservicios mantienen réplicas locales de solo lectura (ej. `DishOperationRef`, `OperationalParameterRef`) actualizadas vía Kafka para evitar llamadas REST síncronas y latencias.
* **Inmutabilidad Financiera (Snapshots):** Los contratos y ventas persisten una "fotografía" de los parámetros operativos y de rentabilidad vigentes en el momento de la firma, blindando los datos históricos ante cambios futuros en las políticas de precios.

## 📦 Estructura de Microservicios

El ERP está compuesto por los siguientes módulos independientes:

1.  **`erp-common`:** Librería central (Core). Contiene el motor de ruteo multi-tenant, la configuración compartida de seguridad (Filtros JWT), JPA, integraciones con Flyway y la implementación del motor Outbox.
2.  **`ms-auth`:** Servicio de identidad y gobierno. Centraliza el login, gestiona usuarios y orquesta el aprovisionamiento en caliente de las bases de datos para los nuevos inquilinos (`TenantProvisioning`).
3.  **`ms-kitchen`:** (Master Data) Corazón de la producción. Gestiona el catálogo maestro de materias primas, alérgenos (normativa UE), escandallos (recetas), platos y plantillas de menú. Propaga los costes reales hacia los demás módulos.
4.  **`ms-operations`:** Logística y control financiero. Recibe reservas y genera Hojas de Servicio (`Function Sheets`) automáticamente. Mantiene las políticas de rentabilidad (Suelos, Techos, Overheads) y aloja el `PricingEngine`.
5.  **`ms-sales`:** Frontend transaccional de ventas. Gestiona clientes, espacios y reservas. Ejecuta simulaciones de precios en tiempo real mediante el `PricingSimulationService` apoyándose en réplicas locales de datos operativos.

## 💡 Flujos Core Destacados

* **Pricing Corridor (Simulador de Precios):** Al crear una reserva, el sistema evalúa el coste real de los platos seleccionados, suma los porcentajes operativos y de riesgo (Overhead), y devuelve un pasillo de precios sugerido y mínimo exigido.
* **Alertas Financieras:** Si un Director de Ventas fuerza la venta de un menú por debajo del coste suelo calculado, el sistema emite una alerta (`DEFICIT_MARGIN_APPROVED`) hacia operaciones o gerencia.
* **Desglose Inteligente (BOM):** Al confirmar una venta en `ms-sales`, `ms-operations` recibe el evento y explosiona el menú contratado en raciones exactas de producción según los comensales por plato.

## 🚀 Tecnologías

* **Backend:** Java 17, Spring Boot 3.2.x, Spring Data JPA / Hibernate
* **Mensajería:** Apache Kafka (KRaft mode) & Spring Kafka
* **Persistencia:** PostgreSQL (Esquema Central + N Esquemas de Inquilinos)
* **Migraciones:** Flyway (Ejecución distribuida por tenant)
* **Seguridad:** JJWT (JSON Web Tokens)
* **Observabilidad:** Micrometer Tracing (Sleuth/Zipkin)