CREATE TABLE `ventas` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `cliente_id`      BIGINT       NOT NULL,
    `producto_id`     INT          NOT NULL,
    `cantidad`        INT          NOT NULL,
    `precio_unitario` INT          NOT NULL,
    `total_venta`     INT          NOT NULL,
    `fecha_venta`     DATETIME     NOT NULL,
    `notas`           VARCHAR(255) NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
