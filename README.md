
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

## 📦 Použité technológie

* **Java 17+**
* **Spring Boot**
* **Spring Data JPA**
* **Hibernate**
* **MySQL**
* **Maven**

## 📁 Štruktúra projektu

```
src/main/java/me/seadlnej/server/
 ├── controller/     # REST kontroléry
 ├── model/          # Entity (Používateľ, Profil, Správa, Konverzácia, Úloha, Notifikácia, ...)
 ├── repository/     # JPA repozitáre
 ├── service/        # Biznis logika
 └── requests/       # Modely pre requesty/response
```

```
src/main/java/me/seadlnej/app/
 ├── core/           # Hlavné scény a UI komponenty
 ├── handlers/       # Spracovanie scén, UI komponentov, dát a komunikácie so serverom
 ├── managers/       # Správa objektov
 ├── providers/      # Poskytovatelia pre textové polia
 ├── resources/      # Zdroje (Súbory, Obrázky, atď.)
 └── utilities/      # Všeobecné pomocné triedy, znovupoužiteľné funkcie a utility metódy
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

## Screenshoty z aplikácie
<img width="1918" height="1136" alt="image" src="https://github.com/user-attachments/assets/17e4e6fe-22cb-4a71-9436-8a7071c6f75d" />

<img width="1919" height="1139" alt="image" src="https://github.com/user-attachments/assets/f33d28ab-81fe-4bac-8115-4c9a6e963ee1" />

<img width="1919" height="1140" alt="image" src="https://github.com/user-attachments/assets/78bb0845-4dad-4070-98f9-dabefb987c7d" />

## Popis výziev a riešení (napr. validácia, autentifikácia)

### Výzvy

1. **Autentifikácia a autorizácia používateľov**  
   Väčšina operácií na serveri vyžaduje overenie identity používateľa, ale používateľ nikdy priamo nepozná svoje interné ID. Jedinými dostupnými identifikátormi sú ID konverzácií alebo správ. To znamená, že všetka autentifikácia a prístup k dátam musí byť bezpečne spracovaná cez server a session tokeny.

2. **Správa session tokenov**  
   Používateľ je autentifikovaný prostredníctvom session tokenu, ktorý má platnosť 30 dní. Správa expirácie tokenu, obnovenie a zabezpečenie proti neoprávnenému prístupu predstavovala výzvu.

3. **Validácia a ochrana dát**  
   Keďže používateľ nikdy nepozná svoje ID, všetky požiadavky museli byť validované na serverovej strane, aby sa zabránilo neoprávnenému prístupu k dátam iných používateľov.

### Riešenia

1. **Použitie session tokenu**  
   Každý používateľský request nesie token, ktorý jednoznačne identifikuje session. Server overuje token a na jeho základe umožní prístup k dátam používateľa (profil, správy, konverzácie). Token je bezpečne uložený a platný 30 dní.

2. **Server-side ID management**  
   Interné ID používateľa sa nikdy neposiela klientovi. Všetky operácie, ktoré vyžadujú identifikáciu používateľa, sú realizované serverom na základe tokenu. Používateľ vidí iba ID konverzácií alebo správ, ktoré sú relevantné pre jeho interakciu.

3. **Bezpečná validácia requestov**  
   Server overuje, že každá akcia (napr. odoslanie správy, aktualizácia profilu) je autorizovaná, t.j. že používateľ s daným tokenom má právo vykonať danú operáciu. Toto rieši problém, že používateľ nepozná svoje ID a zároveň zabraňuje neoprávnenému prístupu k iným dátam.

## Zhodnotenie práce s AI

Veľká časť backendu bola generovaná pomocou AI (ChatGPT a Copilot), hlavne kvôli neznalosti niektorých knižníc a frameworkov. Najväčšia pomoc prišla pri:

- **Repository funkciách** – generovanie CRUD operácií, JPA repository metód.
- **REST controlleroch** – vytváranie endpointov, spracovanie requestov a response objektov.
- **WebSocket implementácii** – celá komunikácia cez WebSocket bola navrhnutá a implementovaná AI, vzhľadom na jej zložitosť a náročnosť pochopenia.

V časti **JavaFX a UI dizajnu** bola AI využitá hlavne pri nastavovaní **CSS pre UI komponenty**, aby vizuálne vyzerali správne a konzistentne.

### Manuálne doladenie

- Backend kód bol následne manuálne prispôsobený štruktúre projektu a interným pravidlám.
- UI komponenty boli doladené, aby správne reagovali na udalosti a zobrazenie bolo responzívne.

### Čo sme sa naučili

- Lepšie pochopenie JPA, REST architektúry a WebSocket komunikácie.
- Efektívne využitie AI na generovanie boilerplate kódu a návrhy riešení, ktoré by inak vyžadovali dlhší čas.
- Schopnosť kombinovať generovaný kód s vlastnou logikou a prispôsobiť ho špecifikám projektu.

## ⚙️ Running the Project

### 1. Konfigurácia databáze v `application.properties`:

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

## 🧪 Testovanie

Použite Postman, Insomnia, alebo akýkoľvek iný REST client na testovanie `/api/...` endpointov.

## 📄 License

This project is open-source and free to use.

---
