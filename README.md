
## 📘 Stručný popis projektu

Táto aplikácia je určená pre **študentov**, ktorí potrebujú jednoduchý a prehľadný spôsob, ako medzi sebou zdieľať študijné materiály – dokumenty, prezentácie, súbory aj fotografie. Umožňuje komunikáciu a zdieľanie **1:1**, ale aj v rámci **skupín**, kde môže každý člen nahrávať a sprístupňovať materiály ostatným.

Aplikácia rieši problém nejednotného a chaotického zdieľania materiálov na rôznych predmetoch, kde prednášajúci alebo cvičiaci používajú rôzne platformy (Moodle, Teams, e-mail, Discord, osobné weby…). Vďaka tejto aplikácii môžu študenti zdieľať všetko na **jednom mieste**, bez ohľadu na to, odkiaľ materiály pochádzajú.

Medzi hlavné výhody patria:

* jednotné miesto pre všetky materiály,
* jednoduché zdieľanie medzi jednotlivcami aj skupinami,
* možnosť vytvárať skupiny pre predmety, ročníky alebo tímy,
* prístup k materiálom pre všetkých členov skupiny.

## 🏗️ Architektúra systému

Aplikácia je postavená na trojvrstvovej architektúre, ktorá pozostáva z **JavaFX frontend klienta**, **Spring Boot backendu** a **MySQL databázy**. Súbory sa ukladajú lokálne na serveri. Komunikácia prebieha cez **REST API** a **WebSocket**.

### ASCII diagram architektúry

```
                    ┌────────────────────────┐
                    │      JavaFX Client     │
                    │  - UI                 │
                    │  - REST komunikácia   │
                    │  - WebSocket klient   │
                    └───────────┬────────────┘
                                │ REST / WS
                                ▼
                   ┌──────────────────────────┐
                   │       Spring Boot        │
                   │     (Backend Server)     │
                   │                          │
                   │  Controllers (REST/WS)   │
                   │  ┌────────────────────┐  │
                   │  │   Service vrstva   │  │
                   │  └────────────────────┘  │
                   │  ┌────────────────────┐  │
                   │  │ Repository vrstva  │  │
                   │  └────────────────────┘  │
                   │                          │
                   └─────────────┬────────────┘
                                 │ JPA / SQL
          ┌──────────────────────┴─────────────────────┐
          ▼                                            ▼
┌──────────────────────┐                    ┌────────────────────────┐
│       MySQL DB       │                    │  Lokálne úložisko      │
│  - úlohy, skupiny    │                    │  - nahrané súbory      │
│  - používatelia      │                    │  - materiály           │
└──────────────────────┘                    └────────────────────────┘
```

### Popis vrstiev

#### **1. JavaFX Frontend**

* poskytuje grafické používateľské rozhranie,
* komunikuje s backendom cez **REST API** na CRUD operácie,
* využíva **WebSocket** na real-time notifikácie (napr. nové súbory, správy),
* spracúva JWT token a session token.

#### **2. Spring Boot Backend**

* poskytuje API pre komunikáciu klienta,
* zabezpečuje autentifikáciu pomocou **JWT**,
* obsahuje WebSocket endpointy pre real-time komunikáciu,
* implementuje biznis logiku vo **service vrstve**,
* využíva **repository vrstvu** pre prácu s databázou.

#### **3. MySQL Databáza**

* ukladá informácie o používateľoch, skupinách, konverzáciách, úlohách a zdieľaných súboroch,
* je napojená cez JPA/Hibernate.

#### **4. Lokálne úložisko súborov**

* server ukladá nahrané súbory do lokálneho filesystemu,
* databáza obsahuje len metadáta (cesta, názov súboru, autor, skupina).








```text
┌────────────┐       ┌────────────────┐       ┌────────────────┐               ┌──────────────┐      ┌───────────────────────┐
│   users    │1     1│  user_profile  │1     N│   user_requests│               │conversations │1    N│conversation_members   │
│────────────│──────>│─────────────── │──────>│─────────────── │               │──────────────│─────>│───────────────────────│
│ id (PK)    │       │ user_id (PK,FK)│       │ id (PK)        │               │ id (PK)      │      │ conversation_id(PK,FK)│
│ firstname  │       │ image          │       │ sender_id (FK) │               │ type         │      │ user_id (PK,FK)       │
│ lastname   │       │ status         │       │ receiver_id(FK)│               │ name         │      │ role                  │
│ username   │       │ address        │       │ status         │               │ description  │      │ joined_at             │
│ passhash   │       │ birthdate      │       │ created_at     │               │ groupImage   │      │ deleted_at            │
│ email      │       │ bio            │       │ responded_at   │               │ createdBy(FK)│      └───────────────────────┘
│ phone      │       └────────────────┘       └────────────────┘               └──────────────┘
└────────────┘                                                                        │
       │                                                                              │ 1:N
       │ 1:N                                                                          ▼
       ▼                                                                        ┌─────────────────────┐
┌───────────────────┐                                                           │conversation_messages│
│ user_contacts     │                                                           │─────────────────────│
│───────────────────│                                                           │ id (PK)             │
│ user_id (PK,FK)   │                                                           │ conversation_id (FK)│
│ contact_id (PK,FK)│                                                           │ sender_id (FK)      │
│ since             │                                                           │ type                │
└───────────────────┘                                                           │ textContent         │
                                                                                │ textContent         │
                                                                                │ media_id (FK)       │
                                                                                │ sent_at             │
                                                                                │ edited_at           │
                                                                                └─────────────────────┘




┌────────────────────┐       ┌───────────────┐
│ tasks              │1    N│ task_assignees │
│────────────────────│──────>│───────────────│
│ id(PK)             │       │ task_id(PK,FK)│
│ conversation_id(FK)│       │ user_id(PK,FK)│
│ creator_id(FK)     │       │ assigned_at   │
│ title              │       └───────────────┘
│ description        │
│ status             │
│ progress           │
│ created_at         │
│ dueDate            │
└────────────────────┘
       │ 1:N
       ▼
┌──────────────┐
│ task_steps   │
│──────────────│
│ id(PK)       │
│ task_id(FK)  │
│ title        │
│ description  │
│ weight       │
│ status       │
│ deadline     │
│ created_at   │
└──────────────┘
```

## 📦 Technologies Used

* **Java 17+**
* **Spring Boot**
* **Spring Data JPA**
* **Hibernate**
* **MySQL / PostgreSQL** (configurable)
* **Maven**

## 📁 Project Structure

```
src/main/java/me/seadlnej/server/
 ├── controller/     # REST controllers
 ├── model/          # Entities (User, Profile, Message, Conversation, Task, Notification, ...)
 ├── repository/     # JPA repositories
 ├── service/        # Business logic
 └── requests/            # Request/response models
```

```
src/main/java/me/seadlnej/app/
 ├── core/           # Main scenes and UI components
 ├── handlers/       # Handler for handeling scenes, UI components, datas and server communication  
 ├── managers/       # Managing objects
 ├── providers/      # Providers for text fields
 ├── resources/      # Resources (Files, Images, etc)
 └── utilities/      # General helper classes, reusable functions, and utility methods
```

# API Endpointy

| Endpoint | Metóda | Popis | Parametre / Telo požiadavky |
|----------|--------|-------|-----------------------------|
| `/api` | POST | Main api endpoint, all endpoint are behind him |
| `/token` | POST | Verification of session token |
| `/users/me` | POST | Getting default informations about user | `token` |
| `/users/login` | POST | Login user | `email/username`, `password` |
| `/users/register` | POST | Register new user | `firstname`, `lastanem`, `username`, `email`, `phone`, `password`, `repeat password` |
| `/users/register-resend` | POST | Send new registration code to email | `email` |
| `/users/register-complete` | POST | Complete registration | `email`, `verification_code` |
| `/users/password-reset/request` | POST | Password reset request | `email` |
| `/users/password-reset/complete` | POST | Password reset complete | `reset_code`, `new_password` |
| `/profile/basic` | POST | Basic informattions about user's account and profile | `username` |
| `/profile/extended` | POST | Extended informattions about user's account and profile | `username` |
| `/profile/status` | POST | Getting activity status of user | `username` |
| `/profile/update` | POST | Update user's profile information | Fields to update (`firstname`, `lastname`, `bio`, `avatar`, etc.) |
| `/request/show` | POST | Show incoming or sent requests | `token` |
| `/request/send` | POST | Send a new request to a user | `username` |
| `/request/respond` | POST | Respond to a request (accept/reject) | `request_id`, `action` (`accept` or `reject`) |
| `/notifications/show` | POST | Show all notifications for the user | `token` |
| `/notifications/delete` | POST | Delete one or more notifications | `notification_id` or list of `notification_ids` |
| `/group/create` | POST | Create a new group | `group_name`, `description`, optional `members` |
| `/group/update` | POST | Update group information | `group_id`, fields to update |
| `/group/users` | POST | List users in a group | `group_id` |
| `/group/add` | POST | Add users to a group | `group_id`, list of `username` |
| `/group/remove` | POST | Remove users from a group | `group_id`, list of `username` |
| `/group/delete` | POST | Delete a group | `group_id` |
| `/chat/contacts` | POST | List chat contacts or active conversations | `token` |
| `/chat/send` | POST | Send a message to a user or group | `conversation_id`, `message_content`, `token` |
| `/chat/delete` | POST | Delete a message or conversation | `message_id`,  `token` |
| `/chat/messages` | POST | List messages with a user or in a group | `conversation_id` |

## ⚙️ Running the Project

### 1. Configure database in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/studyhub
spring.datasource.username=root
spring.datasource.password=yourpass
spring.jpa.hibernate.ddl-auto=update
```

### 2. Build & run

```sh
mvn spring-boot:run
```

## 🧪 Testing

Use Postman, Insomnia, or any REST client to test the `/api` endpoints.

## 📄 License

This project is open-source and free to use.

---
