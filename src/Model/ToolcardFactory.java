package Model;
//TODO: link image_urls via imagepath hier
//maakt toolcardobjecten gemakkelijk aan, deze wordt gebruikt in ToolcardDAO zodat je gemakkelijk een toolcard kan aanmaken met de juiste waarden
public class ToolcardFactory {

    public static IToolcard createToolcard(int id) {
        switch (id) {
            case 1:
                return new Toolcard(1, "Driepuntstang", "Nadat je een dobbelsteen kiest, mag je de waarde ervan met 1 verhogen of verlagen.");
            case 2:
                return new Toolcard(2, "ÉglomiséBorstel", "Verplaats een dobbelsteen in je raam. je mag de voorwaarden voor kleur negeren.  Je moet alle andere voorwaarden nog steeds respecteren.");
            case 3:
                return new Toolcard(3, "Folie-aandrukker", "Verplaats een dobbelsteen in je raam.  je mag de voorwaarden voor waardes negeren. Je moet alle andere voorwaarden nog steeds respecteren.");
            case 4:
                return new Toolcard(4, "Loodopenhaler", "Verplaats exact 2 dobbelstenen. je moet hierbij alle voorwaarden voor de plaatsing respecteren.");
            case 5:
                return new Toolcard(5, "Rondsnijder", "Nadat je een dobbelsteen kiest. Mag je de gekozen dobbelsteen wisselen met een dobbelsteen op het Rondespoor.");
            case 6:
                return new Toolcard(6, "Fluxborstel", "Nadat je een dobbelsteen kiest, mag je hem opnieuw werpen. Als je hem niet kunt plaatsen, leg hem dan terug in het Aanbod.");
            case 7:
                return new Toolcard(7, "Loodhamer", "Werp alle dobbelstenen in het Aanbod opnieuw. Je mag dit enkel doen tijdens je 2e beurt, voor je een steen kiest.");
            case 8:
                return new Toolcard(8, "Glasbreektang", "Na je eerste beurt mag je meteen een tweede dobbelsteen kiezen. Sla deze ronde je 2e beurt over.");
            case 9:
                return new Toolcard(9, "Snijliniaal", "Nadat je een dobbelsteen kiest. mag je hem leggen in een vak dat niet grenst aan een andere steen. Je moet alle andere voorwaarden nog steeds respecteren.");
            case 10:
                return new Toolcard(10, "Schuurblok", "Nadat je een dobbelsteen kiest, mag je hem draaien naar de tegenovergestelde zijde. 6 naar 1, 5 naar 2, 4 naar 3 enz.");
            case 11:
                return new Toolcard(11, "Fluxverwijderaar", "Nadat je een dobbelsteen kiest, mag je hem terug in de zak stoppen en een nieuwe steen uit de zak trekken. Kies een waarde en plaats de nieuwe steen, of leg hem in het Aanbod.");
            case 12:
                return new Toolcard(12, "Olieglassnijder", "Verplaats tot 2 dobbelstenen van dezelfde kleur die overeenkomen met een steen op het Rondespoor. Je moet alle andere voorwaarden nog steeds respecteren.");
            default:
                return null;
        }
    }
}