📘 Stručný popis projektu

Táto aplikácia je určená pre študentov, ktorí potrebujú jednoduchý a prehľadný spôsob, ako medzi sebou zdieľať študijné materiály – dokumenty, prezentácie, súbory aj fotografie. Umožňuje komunikáciu a zdieľanie 1:1, ale aj v rámci skupín, kde môže každý člen nahrávať a sprístupňovať materiály ostatným.

Aplikácia rieši problém nejednotného a chaotického zdieľania materiálov na rôznych predmetoch, kde prednášajúci alebo cvičiaci používajú rôzne platformy (Moodle, Teams, e-mail, Discord, osobné weby…). Vďaka tejto aplikácii môžu študenti zdieľať všetko na jednom mieste, bez ohľadu na to, odkiaľ materiály pochádzajú.

Medzi hlavné výhody patria:
- jednotné miesto pre všetky materiály,
- jednoduché zdieľanie medzi jednotlivcami aj skupinami,
- možnosť vytvárať skupiny pre predmety, ročníky alebo tímy,
- prístup k materiálom pre všetkých členov skupiny.

🏗️ Architektúra systému

Aplikácia je postavená na trojvrstvovej architektúre, ktorá pozostáva z JavaFX frontend klienta, Spring Boot backendu a MySQL databázy. Súbory sa ukladajú lokálne na serveri. Komunikácia prebieha cez REST API a WebSocket.

┌────────────────────────┐
│ JavaFX Client │
│ - UI │
│ - REST komunikácia │
│ - WebSocket klient │
└───────────┬────────────┘
│ REST / WS
▼
┌──────────────────────────┐
│ Spring Boot │
│ (Backend Server) │
│ │
│ Controllers (REST/WS) │
│ ┌────────────────────┐ │
│ │ Service vrstva │ │
│ └────────────────────┘ │
│ ┌────────────────────┐ │
│ │ Repository vrstva │ │
│ └────────────────────┘ │
│ │
└─────────────┬────────────┘
│ JPA / SQL
┌──────────────────────┴─────────────────────┐
▼ ▼
┌──────────────────────┐ ┌────────────────────────┐
│ MySQL DB │ │ Lokálne úložisko │
│ - úlohy, skupiny │ │ - nahrané súbory │
│ - používatelia │ │ - materiály │
└──────────────────────┘ └────────────────────────┘
