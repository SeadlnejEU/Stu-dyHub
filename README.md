
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
│  - používatelia      │                    │  - materiály            │
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
┌────────────┐       ┌────────────────┐       ┌────────────────┐                      ┌──────────────┐      ┌───────────────────────┐
│   users    │1     1│  user_profile  │1     N│   user_requests│                      │conversations │1    N│conversation_members   │
│────────────│──────>│─────────────── │──────>│─────────────── │                      │──────────────│─────>│───────────────────────│
│ id (PK)    │       │ user_id (PK,FK)│       │ id (PK)        │                      │ id (PK)      │      │ conversation_id(PK,FK)│
│ firstname  │       │ image          │       │ sender_id (FK) │                      │ type         │      │ user_id (PK,FK)       │
│ lastname   │       │ status         │       │ receiver_id(FK)│                      │ name         │      │ role                  │
│ username   │       │ address        │       │ status         │                      │ description  │      │ joined_at             │
│ passhash   │       │ birthdate      │       │ created_at     │                      │ groupImage   │      │ deleted_at            │
│ email      │       │ bio            │       │ responded_at   │                      │ createdBy(FK)│      └───────────────────────┘
│ phone      │       └────────────────┘       └────────────────┘                      └──────────────┘
└────────────┘                                                                                │
       │                                                                                      │ 1:N
       │ 1:N                                                                                  ▼
       ▼
┌───────────────────┐                                                                 ┌─────────────────────┐
│ user_contacts     │                                                                 │conversation_messages│
│───────────────────│                                                                 │ id (PK)             │
│ user_id (PK,FK)   │                                                                 │ conversation_id (FK)│
│ contact_id (PK,FK)│                                                                 │ sender_id (FK)      │
│ since             │                                                                 │ type                │
└───────────────────┘                                                                 │ textContent         │
                                                                                      │ textContent         │
                                                                                      │ media_id (FK)       │
                                                                                      │ sent_at             │
                                                                                      │ edited_at           │
                                                                                      └─────────────────────┘




┌────────────────┐
│ media          │
│────────────────│
│ id(PK)         │
│ uploader_id(FK)│
│ fileName │
│ fileType │
│ fileData │
│ mimeType │
│ uploaded_at │
└───────┘

┌───────┐       ┌─────────────┐
│ tasks │1     N│ task_assignees│
│───────│──────>│─────────────│
│ id(PK)│       │ task_id(PK,FK)│
│ conversation_id(FK) │ user_id(PK,FK)│
│ creator_id(FK)│   │ assigned_at    │
│ title │       └─────────────┘
│ description │
│ status      │
│ progress    │
│ created_at  │
│ dueDate     │
└───────┘
       │1
       │N
       ▼
┌───────────┐
│ task_steps│
│───────────│
│ id(PK)    │
│ task_id(FK)│
│ title      │
│ description│
│ weight     │
│ status     │
│ deadline   │
│ created_at │
└───────────┘

┌───────────────┐
│ user_sessions │
│───────────────│
│ id(PK)        │
│ user_id(FK)   │
│ token         │
│ ip            │
│ created_at    │
│ expires_at    │
│ last_used     │
└───────────────┘

┌───────────────┐
│ user_reset    │
│───────────────│
│ id(PK)        │
│ user_id(FK)   │
│ token         │
│ expiry_date   │
└───────────────┘
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
 ├── model/          # Entities (Task, TaskAssignee, TaskSteps, ...)
 ├── repository/     # JPA repositories
 ├── service/        # Business logic
 └── dto/            # Request/response models
```

## 📝 Entities Overview

### **Task**

* id
* conversationId
* creatorId
* title
* description
* status (pending, in_progress, done, cancelled)
* progress (0–100%)
* createdAt
* dueDate

### **TaskCreateRequest**

Used for creating new tasks via REST.

## 📚 API Endpoints (example)

### **Create Task**

**POST** `/tasks`

```json
{
  "conversationId": 1,
  "creatorId": 5,
  "title": "Finish backend",
  "description": "Implement task CRUD endpoints",
  "dueDate": "2025-01-10T10:00:00",
  "assignees": [5, 7, 8]
}
```

## ⚙️ Running the Project

### 1. Configure database in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tasks
spring.datasource.username=root
spring.datasource.password=yourpass
spring.jpa.hibernate.ddl-auto=update
```

### 2. Build & run

```sh
mvn spring-boot:run
```

## 🧪 Testing

Use Postman, Insomnia, or any REST client to test the `/tasks` endpoints.

## 📄 License

This project is open-source and free to use.

---

If chceš, môžem README rozšíriť o obrázky, API tabuľky, databázový ERD alebo inštalačný postup.
