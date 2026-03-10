# Tutorial Review Notes — Offene Punkte

## Dokumentation — noch offen

### exercise-0.md / exercise-1.md — Zertifikate
- Exercise 0 verwendet `browser-certs/Webbrowser_Test_User.p12`, Exercise 1 verwendet `browser-certs/dic/dic-client.p12`. Es ist unklar, ob es hier bewusst zwei verschiedene Zertifikate gibt und wann welches verwendet werden soll.

### exercise-3.md
1. **Titel-Inkonsistenz**: Der Titel sagt "DSF User Role Configuration", die README.md beschreibt Exercise 3 als "DSF User Authentication and Authorization".
2. **Wenig Anleitung zur Versionierung** (Task 3): "Incrementing the resource version to `1.2`" — es wird nicht erklärt, *wo* genau die Version geändert werden muss.

### exercise-4.md
- **Nummerierung der Aufgaben**: Alle Aufgaben nutzen `1.` statt aufsteigende Nummern. Funktioniert in Markdown, ist aber bei manueller Referenzierung verwirrend.

### exercise-7.md
1. **Falsches Zertifikat referenziert?** (Zeile 139): Referenziert `browser-certs/hrp/hrp-client.p12`, aber der Prozess startet an der DIC-Instanz. Sollte das DIC-Zertifikat referenziert werden?
2. **Skeleton-Branch nicht erklärt**: Der Text sagt "The skeleton can be found on the branch `skeleton/exercise-7`", aber es wird nicht klar erklärt, dass man diesen Branch auschecken/mergen muss, bevor man mit der Aufgabe beginnt.

---

## Solution-Branches — fehlende Schritte in Aufgabenstellungen

| Problem | Exercise |
|---------|----------|
| `CosTask`-Bean-Registrierung fehlt in Aufgabenstellung | Exercise 4 |
| `UserVoteListener`-Bean-Registrierung fehlt in Aufgabenstellung | Exercise 7 |
| Inkonsistente Profil-URL (`dsf.dev` vs. `example.org`) in `example-task.xml` der Solution | Exercise 1 |
| Questionnaire-Boilerplate-Items (`business-key`, `user-task-id`) nicht erklärt | Exercise 7 |
| ValueSet-Unterscheidung (`voting-parameters` vs. `voting-results`) unklar | Exercise 7 |
| `Optional.get()` ohne Fallback in Musterlösung — RuntimeException bei fehlendem Input | Exercise 5 |

---

## Code — Designfragen (main Branch)

- **cos-process.bpmn**: `CosTask` ist als generischer `bpmn:task` modelliert, aber `CosTask.java` hat bereits eine vollständige Implementierung. Der BPMN referenziert die Klasse noch nicht, der Code ist aber schon fertig. Könnte Studierende verwirren.
- **HelloHrpMessage.java**: Hat bereits `getAdditionalInputParameters` implementiert, obwohl Exercise 6 Task 5 nur `HelloCosMessage` zum Überschreiben auffordert. Ist als Skeleton gedacht, aber nicht dokumentiert.
