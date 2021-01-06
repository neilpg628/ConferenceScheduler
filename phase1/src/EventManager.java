import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The EventManager class is responsible for handling event-related actions. events is a map that stores
 * the names of events along with its associated event object. rooms is a list of all rooms.
 */
public class EventManager implements Serializable {

    public HashMap<String, Event> events;
    private List<Room> rooms;

    /**
     * Constructs a new EventManager with an empty map of events
     * and an empty list of rooms.
     */
    public EventManager(){
        events = new HashMap<String, Event>();
        rooms =  new ArrayList<Room>();
    }

    /**
     * Creates a new event object.
     * @param name Refers to the name of the event.
     * @param speakerName Refers to the name of the speaker of this event.
     * @param time Refers to the starting time of the event.
     * @param roomNumber Refers to the room number of this event
     * @return Returns the created event.
     */
    public Event createNewEvent(String name, String speakerName, LocalDateTime time, int roomNumber){
        Event event = new Event(name, speakerName, time, roomNumber);
        if (getRoom(roomNumber) == null){
            addRoom(roomNumber);
        }

        return event;
    }

    /**
     * Adds an event to the events map.
     * @param name Refers to the name of the event.
     * @param speakerName Refers to the name of the speaker of this event.
     * @param time Refers to the starting time of the event.
     * @param roomNumber Refers to the room number of this event.
     * @return Returns true if the event is successfully added. Otherwise, it returns false.
     */
    public boolean addEvent(String name, String speakerName, LocalDateTime time, int roomNumber){
        Event event = createNewEvent(name, speakerName, time, roomNumber);
        if (!checkEventIsValid(event)){
            return false;
        }

        events.put(event.getName(), event);
        return true;
    }

    /**
     * @param event Refers to the event being checked.
     * @return Returns True if event is valid, otherwise return false.
     */
    public boolean checkEventIsValid(Event event){

        if (!between9to5(event)){
            return false;
        }
        for (String i: events.keySet()){
            if (event.getName().equals(i)){
                return false;
            }
            Event e = events.get(i);

            boolean invalidTime = compareTimes(event, e);

            if (invalidTime && e.getRoomNumber() == event.getRoomNumber() ){
                return false;
            }
            if (invalidTime && e.getSpeakerName().equals(event.getSpeakerName())){
                return false;
            }
        }
        return true;
    }

    public Set<User> getEventAttendees(String eventName){
        return getEvent(eventName).getAttendeeSet();
    }

    /**
     * Checks if the time of the event is between 9AM and 5PM.
     * @param event Refers to the event being evaluated
     * @return Returns true if the time of the event is between 9AM and 5PM
     */
    private boolean between9to5(Event event){
        LocalDateTime time = event.getTime();

        int year = time.getYear();
        int month = time.getMonthValue();
        int day = time.getDayOfMonth();
        LocalDateTime dateAt9AM = LocalDateTime.of(year, month, day, 9, 0);
        LocalDateTime dateAt4PM = LocalDateTime.of(year, month, day, 16, 0);

        int compare1 = time.compareTo(dateAt9AM);
        int compare2 = time.compareTo(dateAt4PM);

        return (compare1 >= 0 && compare2 <= 0);
    }

    /**
     * Checks if the time of a LocalDateTime object is between 9AM and 5PM.
     * @param time Refers to the time being evaluated
     * @return Returns true if the time of the LocalDateTime object is between 9AM and 5PM
     */

    public boolean between9to5(LocalDateTime time){
        int year = time.getYear();
        int month = time.getMonthValue();
        int day = time.getDayOfMonth();
        LocalDateTime dateAt9AM = LocalDateTime.of(year, month, day, 9, 0);
        LocalDateTime dateAt4PM = LocalDateTime.of(year, month, day, 16, 0);

        int compare1 = time.compareTo(dateAt9AM);
        int compare2 = time.compareTo(dateAt4PM);

        return (compare1 >= 0 && compare2 <= 0);
    }
    /**
     * @param e1 Refers to the event that is requested to be added.
     * @param e2 Refers to an already scheduled event.
     * @return Returns true if the requested time of e1 conflicts the scheduled time of e2. Otherwise returns false.
     */
    public boolean compareTimes(Event e1, Event e2){
        LocalDateTime beginningTime1 = e1.getTime();
        LocalDateTime endTime1 = e1.getTime().plusMinutes(59);

        LocalDateTime beginningTime2 = e2.getTime();
        LocalDateTime endTime2 = e2.getTime().plusMinutes(59);

        int compare1 = beginningTime1.compareTo(beginningTime2);
        int compare2 = beginningTime1.compareTo(endTime2);

        int compare3 = endTime1.compareTo(beginningTime2);
        int compare4 = endTime1.compareTo(endTime2);

        if (compare1 == 0 || compare3 == 0 || (compare1 > 0 && compare2 < 0) || (compare3 > 0 && compare4 < 0)){
            return true;
        }
        return false;
    }


    /**
     * @param time Refers to the time that you want to see if it is after the current time.
     * @return Returns true if the time is after the current time, otherwise returns false.
     */
    public boolean checkTimeIsAfterNow(LocalDateTime time){
        LocalDateTime currentTime = LocalDateTime.now();

        int compare = time.compareTo(currentTime);

        if (compare > 0){
            return true;
        }
        return false;
    }

    /**
     * Creates a new Speaker user.
     * @param name Refers to the name of the speaker.
     * @param address Refers to the address of the speaker.
     * @param email Refers to the email of the speaker.
     * @param username Refers to the username of the speaker.
     * @param password Refers to the password of the speaker.
     * @return Returns the created speaker.
     */
    public Speaker createNewSpeaker(String name, String address, String email, String username, String password){
        return new Speaker(name, address, email, username, password);
    }

    /**
     * Finds and gets the specified event from the map of events.
     * @param eventName Refers the name of the event.
     * @return Returns the specified event.
     */
    public Event getEvent(String eventName){
        for (String i : events.keySet()){
            if (i.equals(eventName)){
                return events.get(i);
            }
        }
        return null;
    }

    /**
     * @return Returns map of events.
     */
    public HashMap<String, Event> getAllEvents(){
        return events;
    }

    public List<String> getAllEventNamesOnly(){
        List<String> allNames = new ArrayList<>();
        allNames.addAll(events.keySet());
        return allNames;
    }

    /**
     * @return Returns list of all rooms
     */
    public List<Room> getRooms(){
        return this.rooms;
    }
    /**
     * Sets events to the events passed in
     * @param events Refers to the events
     */
    public void setAllEvents(HashMap<String, Event> events){
        this.events = events;
    }

    /**
     * Sets rooms to the rooms passed in
     * @param rooms Refers to the rooms
     */
    public void setRooms(ArrayList<Room> rooms){
        this.rooms = rooms;
    }

    /**
     * Checks if the specified event is full.
     * @param eventName Refers to the name of the event.
     * @return Returns true if event is full. Otherwise, returns false.
     */
    public boolean checkEventFull(String eventName){
        Event event = this.getEvent(eventName);

        return (event == null || event.getAttendeeSet().size() == getRoom(event.getRoomNumber()).getCapacity());
    }

    /**
     * Adds a room to the list of rooms.
     * @param roomNumber Refers to the room number.
     * @param capacity Refers to the capacity of the room.
     * @return Returns true if adding room was successful. Otherwise, returns false.
     */
    public boolean addRoom(int roomNumber, int capacity){
        Room room = createNewRoom(roomNumber, capacity);
        int i = 0;
        while (i < rooms.size()){
            if (rooms.get(i).getRoomNumber() == room.getRoomNumber()) {
                return false;
            }
            i++;
        }
        rooms.add(room);
        return true;
    }

    /**
     * Adds a room to the list of rooms (Fixed Capacity).
     * @param roomNumber Refers to the room number.
     * @return Returns true if adding room was successful. Otherwise, returns false.
     */
    public boolean addRoom(int roomNumber){
        Room room = createNewRoom(roomNumber);
        int i = 0;
        while (i < rooms.size()){
            if (rooms.get(i).getRoomNumber() == room.getRoomNumber()) {
                return false;
            }
            i++;
        }
        rooms.add(room);
        return true;
    }

    /**
     * Creates a new room.
     * @param roomNumber Refers to the room number.
     * @param capacity Refers to the capacity of the room.
     * @return Returns the room object.
     */
    public Room createNewRoom(int roomNumber, int capacity){
        Room room = new Room(roomNumber, capacity);
        return room;
    }

    /**
     * Creates a new room (Fixed Capacity).
     * @param roomNumber Refers to the room number.
     * @return Returns the room object.
     */
    public Room createNewRoom(int roomNumber){
        Room room = new Room(roomNumber);
        return room;
    }

    /**
     * Finds and gets the specified room from the list of rooms.
     * @param roomNumber Refers to the room number.
     * @return Returns the specified room.
     */
    public Room getRoom(int roomNumber){
        for (Room room: rooms){
            if (room.getRoomNumber() == roomNumber){
                return room;
            }
        }
        //Room Not Found
        return null;
    }

    /**
     * Yields the events that have already happened.
     * @param allEvents Refers to all the events in this conference.
     * @return All the events that have already happened.
     */
    public List<String> eventHappened(List<String> allEvents){
        LocalDateTime time = LocalDateTime.now();
        List<String> priorEvents = new ArrayList<>();
        for(String event: allEvents){
            Event check = getEvent(event);
            int compare = time.compareTo(check.getTime());
            if (compare > 0){
                priorEvents.add(event);
            }
        }
        return priorEvents;
    }

    /**
     * Yields the events that have yet to happen.
     * @param allEvents Refers to all the events in this conference.
     * @return All the events that have yet to happen
     */
    public List<String> eventNotHappened(List<String> allEvents){
        LocalDateTime time = LocalDateTime.now();
        List<String> futureEvents = new ArrayList<>();
        for(String event: allEvents){
            Event check = getEvent(event);
            int compare = time.compareTo(check.getTime());
            if (compare <= 0){
                futureEvents.add(event);
            }
        }
        return futureEvents;
    }

    /**
     * Returns a List of events in chronological order based on the inputted event names
     * @param theseEvents Names of the events to be sorted
     * @return The list of chronologically sorted events
     */
    public List<Event> chronologicalEvents(List<String> theseEvents){
        List<Event> onlyEvents = new ArrayList<>();
        for (String key : theseEvents) {
            onlyEvents.add(events.get(key));
        }
        Collections.sort(onlyEvents);
        return onlyEvents;
    }

    /**
     * Retrieves the date and time an event occurs
     * @param event the event we want to get the date time for
     * @return The event's date time
     */
    public LocalDateTime getTime(Event event){
        return event.getTime();
    }

    /**
     * Adds an user to the list of an event's attendees.
     * @param event the event who's list of attendees we want to update.
     * @param user the user being added to the list of attendees.
     */
    public void addAttendee(Event event, User user) {
           event.addAttendee(user);
    }
}
