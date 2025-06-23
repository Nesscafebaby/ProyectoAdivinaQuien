-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 23-06-2025 a las 09:25:53
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `personajes`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `partidas`
--

CREATE TABLE `partidas` (
  `id` int(11) NOT NULL,
  `jugador` varchar(100) NOT NULL,
  `personaje_ganador` varchar(100) NOT NULL,
  `fecha_hora` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `partidas`
--

INSERT INTO `partidas` (`id`, `jugador`, `personaje_ganador`, `fecha_hora`) VALUES
(1, 'CEsar', 'Raton', '2025-06-22 17:10:09'),
(2, 'Laura', 'Carlos Slim', '2025-06-15 14:30:00'),
(3, 'Carlos', 'Kratos', '2025-06-16 16:10:00'),
(4, 'Ana', 'Catrina', '2025-06-17 13:45:00'),
(5, 'Miguel', 'Lecler', '2025-06-18 11:20:00'),
(6, 'Elena', 'HHH', '2025-06-19 15:50:00'),
(7, 'Luis', 'HBK', '2025-06-20 17:05:00'),
(8, 'Sofía', 'Coco', '2025-06-21 12:30:00'),
(9, 'Jorge', 'John Cena', '2025-06-22 18:40:00'),
(10, 'jansd', 'Quetzalcoatl', '2025-06-22 23:03:05'),
(11, 'lalo', 'Dom', '2025-06-23 00:18:34'),
(12, 'asdf', 'Cesar', '2025-06-23 01:09:26');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `partidas`
--
ALTER TABLE `partidas`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `partidas`
--
ALTER TABLE `partidas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
