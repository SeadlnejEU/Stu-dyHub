package me.seadlnej.app.managers.contact;

import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import me.seadlnej.app.handlers.session.SessionHandler;
import me.seadlnej.app.handlers.api.ApiHandler;
import me.seadlnej.app.utilities.Base64Converter;
import me.seadlnej.app.utilities.JSON;

import java.time.LocalDateTime;
import java.util.*;

public class ContactsManager {

    // VBox container for tiles
    private final VBox containerAll;
    private final VBox containerDirect;
    private final VBox containerGroups;

    // Lists of contacts
    private final List<Contact> contacts;
    private final List<Contact> directContacts;
    private final List<Contact> groupContacts;

    // Tiles maps
    private final Map<Contact, ContactTile> allTiles;
    private final Map<Contact, ContactTile> directTiles;
    private final Map<Contact, ContactTile> groupTiles;

    private final ApiHandler api = new ApiHandler(); // Api handler

    // Constructor
    public ContactsManager() {
        this.containerAll = new VBox(15);
        this.containerDirect = new VBox(15);
        this.containerGroups = new VBox(15);

        this.contacts = new ArrayList<>();
        this.directContacts = new ArrayList<>();
        this.groupContacts = new ArrayList<>();

        this.allTiles = new HashMap<>();
        this.directTiles = new HashMap<>();
        this.groupTiles = new HashMap<>();

        fetch();
        buildContainers();
    }

    // Refresh UI
    public void refresh() {
        fetch();
    }

    // Fetches data from server
    public void fetch() {

        JSON response = api.post("chat/contacts", SessionHandler.getInstance().getToken());

        if (!response.isSuccess()) { return; }

        // Creating tile for every contacts
        for (String key : response.getJson("contacts").getData().keySet()) {
            JSON conv = response.getJson("contacts").getJson(key);

            if (conv == null) { continue; }

            String type = conv.get("type");

            // Checking if image is correct
            Image image;
            String base64 = conv.get("image");
            if (base64 != null && !base64.isBlank()) {
                image = Base64Converter.base64ToImage(base64);
            } else {
                image = new Image(Objects.requireNonNull(getClass().getResource("/images/user.png")).toString());
            }

            // Validating conv id
            Object idObj = conv.getObj("id");
            long id; if (idObj instanceof Number) { id = ((Number) idObj).longValue(); } else { id = 0; }

            // Validating time
            String sTime = conv.get("lastSent");
            if (sTime == null || sTime.isBlank()) {
                sTime = LocalDateTime.now().toString();
            }

            LocalDateTime time;
            try {
                time = LocalDateTime.parse(sTime); // predpokladá ISO_LOCAL_DATE_TIME
            } catch (Exception e) {
                time = LocalDateTime.now();
            }

            // Creating contact object
            Contact contact;
            if ("direct".equals(type)) {
                contact = new Contact(conv.get("firstname"), conv.get("lastname"), conv.get("username"),
                        conv.get("status"), id, conv.get("lastMessage"), false, time, image
                );
                directContacts.add(contact);
            } else {
                contact = new Contact(id, conv.get("name"), conv.get("last"), false, LocalDateTime.now(), image);
                groupContacts.add(contact);
            }

            contacts.add(contact);

        }
    }

    // Build list of tiles
    public  void buildContainers() {
        containerAll.getChildren().clear();
        containerDirect.getChildren().clear();
        containerGroups.getChildren().clear();

        // Sort contacts by last message time
        contacts.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));
        directContacts.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));
        groupContacts.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));

        // Add tiles
        for (Contact c : contacts) {
            ContactTile tile = new ContactTile(c);
            allTiles.put(c, tile);
            containerAll.getChildren().add(tile);
        }

        for (Contact c : directContacts) {
            ContactTile tile = new ContactTile(c);
            directTiles.put(c, tile);
            containerDirect.getChildren().add(tile);
        }

        for (Contact c : groupContacts) {
            ContactTile tile = new ContactTile(c);
            groupTiles.put(c, tile);
            containerGroups.getChildren().add(tile);
        }

    }

    // Filterer container
    public VBox getFilteredContainer(List<Contact> sourceContacts, String filter) {
        VBox filteredBox = new VBox(15);
        List<Contact> filtered = new ArrayList<>();

        if (filter == null || filter.isEmpty()) {
            filtered.addAll(sourceContacts);
            filtered.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));
        } else {
            String lowerFilter = filter.toLowerCase();
            for (Contact c : sourceContacts) {
                String fullName = (c.getFirstname() + " " + c.getLastname()).toLowerCase();
                if (fullName.contains(lowerFilter)) filtered.add(c);
            }
            filtered.sort(Comparator.comparing(Contact::getFirstname).thenComparing(Contact::getLastname));
        }

        for (Contact c : filtered) {
            // Create new tile for filtered container to avoid duplicate Node issue
            filteredBox.getChildren().add(new ContactTile(c));
        }

        return filteredBox;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public VBox getContainerAll() { return containerAll; }
    public VBox getContainerDirect() { return containerDirect; }
    public VBox getContainerGroups() { return containerGroups; }

}