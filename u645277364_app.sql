-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : sam. 23 mai 2026 à 02:49
-- Version du serveur : 11.8.6-MariaDB-log
-- Version de PHP : 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `u645277364_app`
--

-- --------------------------------------------------------

--
-- Structure de la table `appointments`
--

CREATE TABLE `appointments` (
  `id` varchar(255) NOT NULL,
  `client_user_id` bigint(20) UNSIGNED NOT NULL,
  `lawyer_user_id` bigint(20) UNSIGNED NOT NULL,
  `date` date DEFAULT NULL,
  `time` time DEFAULT NULL,
  `duration_min` int(11) NOT NULL DEFAULT 60,
  `type` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `cancellation_reason` varchar(255) DEFAULT NULL,
  `price` decimal(12,2) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `cache`
--

CREATE TABLE `cache` (
  `key` varchar(255) NOT NULL,
  `value` mediumtext NOT NULL,
  `expiration` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `cache_locks`
--

CREATE TABLE `cache_locks` (
  `key` varchar(255) NOT NULL,
  `owner` varchar(255) NOT NULL,
  `expiration` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `clients`
--

CREATE TABLE `clients` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `profile_id` bigint(20) UNSIGNED NOT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `consultations`
--

CREATE TABLE `consultations` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `client_id` bigint(20) UNSIGNED NOT NULL,
  `lawyer_id` bigint(20) UNSIGNED NOT NULL,
  `status` varchar(255) NOT NULL DEFAULT 'pending',
  `subject` varchar(255) NOT NULL,
  `date` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `conversations`
--

CREATE TABLE `conversations` (
  `id` varchar(255) NOT NULL,
  `lawyer_user_id` bigint(20) UNSIGNED NOT NULL,
  `client_user_id` bigint(20) UNSIGNED NOT NULL,
  `unread_count_user` int(11) NOT NULL DEFAULT 0,
  `unread_count_lawyer` int(11) NOT NULL DEFAULT 0,
  `last_message_content` text DEFAULT NULL,
  `last_message_sent_at` datetime DEFAULT NULL,
  `last_message_sender_id` bigint(20) UNSIGNED DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `device_tokens`
--

CREATE TABLE `device_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `token` varchar(255) NOT NULL,
  `platform` varchar(20) NOT NULL DEFAULT 'android',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `documents`
--

CREATE TABLE `documents` (
  `id` varchar(255) NOT NULL,
  `client_id` bigint(20) UNSIGNED DEFAULT NULL,
  `lawyer_id` bigint(20) UNSIGNED DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) NOT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `file_size_kb` int(11) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `upload_date` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `document_shares`
--

CREATE TABLE `document_shares` (
  `id` varchar(255) NOT NULL,
  `document_id` varchar(255) NOT NULL,
  `lawyer_id` bigint(20) UNSIGNED DEFAULT NULL,
  `expires_at` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `dossiers`
--

CREATE TABLE `dossiers` (
  `id` varchar(255) NOT NULL,
  `case_number` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `opening_date` date DEFAULT NULL,
  `lawyer_id` bigint(20) UNSIGNED DEFAULT NULL,
  `progress` int(11) NOT NULL DEFAULT 0,
  `lawyer_name` varchar(255) DEFAULT NULL,
  `lawyer_specialty` varchar(255) DEFAULT NULL,
  `client_name` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `invoices`
--

CREATE TABLE `invoices` (
  `id` varchar(255) NOT NULL,
  `payment_id` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `amount_text` varchar(255) DEFAULT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  `lawyer_name` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `jobs`
--

CREATE TABLE `jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `queue` varchar(255) NOT NULL,
  `payload` longtext NOT NULL,
  `attempts` tinyint(3) UNSIGNED NOT NULL,
  `reserved_at` int(10) UNSIGNED DEFAULT NULL,
  `available_at` int(10) UNSIGNED NOT NULL,
  `created_at` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `job_batches`
--

CREATE TABLE `job_batches` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `total_jobs` int(11) NOT NULL,
  `pending_jobs` int(11) NOT NULL,
  `failed_jobs` int(11) NOT NULL,
  `failed_job_ids` longtext NOT NULL,
  `options` mediumtext DEFAULT NULL,
  `cancelled_at` int(11) DEFAULT NULL,
  `created_at` int(11) NOT NULL,
  `finished_at` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `lawyers`
--

CREATE TABLE `lawyers` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `profile_id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `speciality` varchar(255) DEFAULT NULL,
  `domaine` varchar(255) DEFAULT NULL,
  `specializations` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`specializations`)),
  `schedule` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`schedule`)),
  `slot_duration_min` int(11) NOT NULL DEFAULT 60,
  `buffer_between_min` int(11) NOT NULL DEFAULT 15,
  `location` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `rating` decimal(3,2) DEFAULT NULL,
  `review_count` int(11) NOT NULL DEFAULT 0,
  `years_experience` int(11) NOT NULL DEFAULT 0,
  `is_verified` tinyint(1) NOT NULL DEFAULT 0,
  `is_available` tinyint(1) NOT NULL DEFAULT 1,
  `bar_number` varchar(255) DEFAULT NULL,
  `bio` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `live_comments`
--

CREATE TABLE `live_comments` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `live_session_id` varchar(255) NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `author_id` bigint(20) UNSIGNED DEFAULT NULL,
  `author_name` varchar(255) DEFAULT NULL,
  `content` text NOT NULL,
  `sent_at` datetime NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `live_sessions`
--

CREATE TABLE `live_sessions` (
  `id` varchar(255) NOT NULL,
  `lawyer_user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `lawyer_name` varchar(255) DEFAULT NULL,
  `topic` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `viewer_count` int(11) NOT NULL DEFAULT 0,
  `participants` int(11) NOT NULL DEFAULT 0,
  `thumbnail_url` varchar(255) DEFAULT NULL,
  `stream_url` varchar(255) DEFAULT NULL,
  `stream_key` varchar(255) DEFAULT NULL,
  `rtmp_url` varchar(255) DEFAULT NULL,
  `playback_url` varchar(255) DEFAULT NULL,
  `started_at` datetime DEFAULT NULL,
  `scheduled_at` datetime DEFAULT NULL,
  `duration_sec` int(11) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `messages`
--

CREATE TABLE `messages` (
  `id` varchar(255) NOT NULL,
  `conversation_id` varchar(255) NOT NULL,
  `sender_id` bigint(20) UNSIGNED NOT NULL,
  `content` text DEFAULT NULL,
  `type` varchar(255) NOT NULL DEFAULT 'text',
  `file_url` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `is_from_user` tinyint(1) NOT NULL DEFAULT 1,
  `sent_at` datetime NOT NULL,
  `sender_name` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `entity_type` varchar(255) DEFAULT NULL,
  `entity_id` varchar(255) DEFAULT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `payments`
--

CREATE TABLE `payments` (
  `id` varchar(255) NOT NULL,
  `client_id` bigint(20) UNSIGNED DEFAULT NULL,
  `lawyer_id` bigint(20) UNSIGNED DEFAULT NULL,
  `appointment_id` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `amount_text` varchar(255) DEFAULT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  `currency` varchar(255) NOT NULL DEFAULT 'MAD',
  `paid_at` datetime DEFAULT NULL,
  `invoice_url` varchar(255) DEFAULT NULL,
  `gateway_response` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`gateway_response`)),
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `profiles`
--

CREATE TABLE `profiles` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'CLIENT',
  `address` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `reels`
--

CREATE TABLE `reels` (
  `id` varchar(255) NOT NULL,
  `lawyer_user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `lawyer_name` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `caption` text DEFAULT NULL,
  `video_url` varchar(255) NOT NULL,
  `thumbnail_url` varchar(255) DEFAULT NULL,
  `likes_count` int(11) NOT NULL DEFAULT 0,
  `views_count` int(11) NOT NULL DEFAULT 0,
  `duration` varchar(255) DEFAULT NULL,
  `duration_sec` int(11) DEFAULT NULL,
  `trend` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `reel_likes`
--

CREATE TABLE `reel_likes` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `reel_id` varchar(255) NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `refund_requests`
--

CREATE TABLE `refund_requests` (
  `id` varchar(255) NOT NULL,
  `payment_id` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `reason` text NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `sessions`
--

CREATE TABLE `sessions` (
  `id` varchar(255) NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` text DEFAULT NULL,
  `payload` longtext NOT NULL,
  `last_activity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `stories`
--

CREATE TABLE `stories` (
  `id` varchar(255) NOT NULL,
  `lawyer_user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `lawyer_name` varchar(255) DEFAULT NULL,
  `media_url` varchar(255) NOT NULL,
  `caption` text DEFAULT NULL,
  `expires_at` datetime DEFAULT NULL,
  `is_live` tinyint(1) NOT NULL DEFAULT 0,
  `views` int(11) NOT NULL DEFAULT 0,
  `time_left` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `remember_token` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'CLIENT',
  `status` varchar(30) NOT NULL DEFAULT 'active',
  `phone` varchar(255) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `appointments_client_user_id_foreign` (`client_user_id`),
  ADD KEY `appointments_lawyer_user_id_foreign` (`lawyer_user_id`);

--
-- Index pour la table `cache`
--
ALTER TABLE `cache`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_expiration_index` (`expiration`);

--
-- Index pour la table `cache_locks`
--
ALTER TABLE `cache_locks`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_locks_expiration_index` (`expiration`);

--
-- Index pour la table `clients`
--
ALTER TABLE `clients`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `clients_profile_id_unique` (`profile_id`),
  ADD KEY `clients_user_id_foreign` (`user_id`);

--
-- Index pour la table `consultations`
--
ALTER TABLE `consultations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `consultations_client_id_foreign` (`client_id`),
  ADD KEY `consultations_lawyer_id_foreign` (`lawyer_id`);

--
-- Index pour la table `conversations`
--
ALTER TABLE `conversations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `conversations_lawyer_user_id_foreign` (`lawyer_user_id`),
  ADD KEY `conversations_client_user_id_foreign` (`client_user_id`),
  ADD KEY `conversations_last_message_sender_id_foreign` (`last_message_sender_id`);

--
-- Index pour la table `device_tokens`
--
ALTER TABLE `device_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `device_tokens_token_unique` (`token`),
  ADD KEY `device_tokens_user_id_foreign` (`user_id`);

--
-- Index pour la table `documents`
--
ALTER TABLE `documents`
  ADD PRIMARY KEY (`id`),
  ADD KEY `documents_client_id_foreign` (`client_id`),
  ADD KEY `documents_lawyer_id_foreign` (`lawyer_id`);

--
-- Index pour la table `document_shares`
--
ALTER TABLE `document_shares`
  ADD PRIMARY KEY (`id`),
  ADD KEY `document_shares_document_id_foreign` (`document_id`),
  ADD KEY `document_shares_lawyer_id_foreign` (`lawyer_id`);

--
-- Index pour la table `dossiers`
--
ALTER TABLE `dossiers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `dossiers_lawyer_id_foreign` (`lawyer_id`);

--
-- Index pour la table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Index pour la table `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`id`),
  ADD KEY `invoices_payment_id_foreign` (`payment_id`);

--
-- Index pour la table `jobs`
--
ALTER TABLE `jobs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `jobs_queue_index` (`queue`);

--
-- Index pour la table `job_batches`
--
ALTER TABLE `job_batches`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `lawyers`
--
ALTER TABLE `lawyers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `lawyers_profile_id_unique` (`profile_id`),
  ADD KEY `lawyers_user_id_foreign` (`user_id`);

--
-- Index pour la table `live_comments`
--
ALTER TABLE `live_comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `live_comments_author_id_foreign` (`author_id`),
  ADD KEY `live_comments_live_session_id_sent_at_index` (`live_session_id`,`sent_at`),
  ADD KEY `live_comments_user_id_foreign` (`user_id`);

--
-- Index pour la table `live_sessions`
--
ALTER TABLE `live_sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `live_sessions_lawyer_user_id_foreign` (`lawyer_user_id`);

--
-- Index pour la table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `messages_sender_id_foreign` (`sender_id`),
  ADD KEY `messages_conversation_id_sent_at_index` (`conversation_id`,`sent_at`);

--
-- Index pour la table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `notifications_user_id_foreign` (`user_id`);

--
-- Index pour la table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`email`);

--
-- Index pour la table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `payments_client_id_foreign` (`client_id`),
  ADD KEY `payments_lawyer_id_foreign` (`lawyer_id`),
  ADD KEY `payments_appointment_id_foreign` (`appointment_id`);

--
-- Index pour la table `profiles`
--
ALTER TABLE `profiles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `profiles_user_id_unique` (`user_id`);

--
-- Index pour la table `reels`
--
ALTER TABLE `reels`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reels_lawyer_user_id_foreign` (`lawyer_user_id`);

--
-- Index pour la table `reel_likes`
--
ALTER TABLE `reel_likes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reel_likes_reel_id_user_id_unique` (`reel_id`,`user_id`),
  ADD KEY `reel_likes_user_id_foreign` (`user_id`);

--
-- Index pour la table `refund_requests`
--
ALTER TABLE `refund_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `refund_requests_payment_id_foreign` (`payment_id`),
  ADD KEY `refund_requests_user_id_foreign` (`user_id`);

--
-- Index pour la table `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sessions_user_id_index` (`user_id`),
  ADD KEY `sessions_last_activity_index` (`last_activity`);

--
-- Index pour la table `stories`
--
ALTER TABLE `stories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `stories_lawyer_user_id_foreign` (`lawyer_user_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`),
  ADD KEY `users_role_index` (`role`),
  ADD KEY `users_status_index` (`status`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `clients`
--
ALTER TABLE `clients`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `consultations`
--
ALTER TABLE `consultations`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `device_tokens`
--
ALTER TABLE `device_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `jobs`
--
ALTER TABLE `jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `lawyers`
--
ALTER TABLE `lawyers`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `live_comments`
--
ALTER TABLE `live_comments`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `profiles`
--
ALTER TABLE `profiles`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `reel_likes`
--
ALTER TABLE `reel_likes`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `appointments`
--
ALTER TABLE `appointments`
  ADD CONSTRAINT `appointments_client_user_id_foreign` FOREIGN KEY (`client_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `appointments_lawyer_user_id_foreign` FOREIGN KEY (`lawyer_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `clients`
--
ALTER TABLE `clients`
  ADD CONSTRAINT `clients_profile_id_foreign` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `clients_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `consultations`
--
ALTER TABLE `consultations`
  ADD CONSTRAINT `consultations_client_id_foreign` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `consultations_lawyer_id_foreign` FOREIGN KEY (`lawyer_id`) REFERENCES `lawyers` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `conversations`
--
ALTER TABLE `conversations`
  ADD CONSTRAINT `conversations_client_user_id_foreign` FOREIGN KEY (`client_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `conversations_last_message_sender_id_foreign` FOREIGN KEY (`last_message_sender_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `conversations_lawyer_user_id_foreign` FOREIGN KEY (`lawyer_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `device_tokens`
--
ALTER TABLE `device_tokens`
  ADD CONSTRAINT `device_tokens_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `documents`
--
ALTER TABLE `documents`
  ADD CONSTRAINT `documents_client_id_foreign` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `documents_lawyer_id_foreign` FOREIGN KEY (`lawyer_id`) REFERENCES `lawyers` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `document_shares`
--
ALTER TABLE `document_shares`
  ADD CONSTRAINT `document_shares_document_id_foreign` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `document_shares_lawyer_id_foreign` FOREIGN KEY (`lawyer_id`) REFERENCES `lawyers` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `dossiers`
--
ALTER TABLE `dossiers`
  ADD CONSTRAINT `dossiers_lawyer_id_foreign` FOREIGN KEY (`lawyer_id`) REFERENCES `lawyers` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `invoices_payment_id_foreign` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `lawyers`
--
ALTER TABLE `lawyers`
  ADD CONSTRAINT `lawyers_profile_id_foreign` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `lawyers_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `live_comments`
--
ALTER TABLE `live_comments`
  ADD CONSTRAINT `live_comments_author_id_foreign` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `live_comments_live_session_id_foreign` FOREIGN KEY (`live_session_id`) REFERENCES `live_sessions` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `live_comments_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `live_sessions`
--
ALTER TABLE `live_sessions`
  ADD CONSTRAINT `live_sessions_lawyer_user_id_foreign` FOREIGN KEY (`lawyer_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_conversation_id_foreign` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `messages_sender_id_foreign` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_appointment_id_foreign` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `payments_client_id_foreign` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `payments_lawyer_id_foreign` FOREIGN KEY (`lawyer_id`) REFERENCES `lawyers` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `profiles`
--
ALTER TABLE `profiles`
  ADD CONSTRAINT `profiles_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `reels`
--
ALTER TABLE `reels`
  ADD CONSTRAINT `reels_lawyer_user_id_foreign` FOREIGN KEY (`lawyer_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `reel_likes`
--
ALTER TABLE `reel_likes`
  ADD CONSTRAINT `reel_likes_reel_id_foreign` FOREIGN KEY (`reel_id`) REFERENCES `reels` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reel_likes_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `refund_requests`
--
ALTER TABLE `refund_requests`
  ADD CONSTRAINT `refund_requests_payment_id_foreign` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `refund_requests_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `stories`
--
ALTER TABLE `stories`
  ADD CONSTRAINT `stories_lawyer_user_id_foreign` FOREIGN KEY (`lawyer_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
