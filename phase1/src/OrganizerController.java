import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Refers to the controller class that will deal with the actions of an organizer object.
 */
public class OrganizerController extends AttendeeController {


    /**
     * Constructs an OrganizerController object.
     * @param userManager Refers to the UserManager object.
     * @param eventManager Refers to the EventManager object.
     * @param messageManager Refers to the MessageManager object.
     * @param username Refers to the username of the organizer.
     */
    public OrganizerController(UserManager userManager, EventManager eventManager, MessageManager messageManager, String username) {
        super(userManager, eventManager, messageManager, username);
    }



    /**
     * Runs the OrganizerController by asking for input and performing the actions
     */
    public void run(){
        p.displayOptions2();
        p.displayTaskInput();
        final int END_CONDITION = 20;
        int input = nextInt();
        while (input != END_CONDITION){ // 20 is ending condition
            determineInput(input);
            input = nextInt();
        }
    }

    private void determineInput(int input) {
        switch (input) {
            case 0:
                viewMessages(this.username);
                break;

            case 1:
                if(userManager.getUserMap().size() == 1) {
                    p.displayConferenceError();
                    break;
                }
                p.displayMethodPrompt();
                // System.out.println("Who would you like to message? (Please enter the username of the recipient). Otherwise, type 'q' to exit");
                String recipient = scan.nextLine();
                if (recipient.equals("q")){
                    break;
                }
                else if(messageManager.checkIsMessageable(recipient, this.username, userManager)) {
                    p.displayEnterMessagePrompt(recipient);
                    // System.out.println("What message would you like to send to: " + recipient + ". " + "If you would no longer like to send a message, type 'q' to exit");
                    String messageContents = scan.nextLine();
                    if (messageContents.equals("q")){
                        break;
                    }
                    sendMessage(recipient, messageContents);
                    p.displayMessageSentPrompt();
                }
                else{
                    p.displayNotMessageableError();
                }
                break;

            case 2:
                if(messageManager.getAllUserMessages().get(this.username).size() == 0){
                    p.displayNoReply();
                    break;
                }
                else if(userManager.getUserMap().size() == 1) {
                    p.displayConferenceError();
                    break;
                }
                List<String> attendees = getSenders(username);
                p.displayAllSenders(attendees);
                p.displayEnterUserUsernamePrompt();
                // System.out.println("Which user are you replying to (it is case sensitive). If you no longer want to reply to a user, type 'q' to exit: ");
                String recipients = scan.nextLine();
                while (!attendees.contains(recipients)){
                    p.displayUserReplyError();
                    recipients = scan.nextLine();
                    if (recipients.equals("q")){
                        break;
                    }
                }
                if (recipients.equals("q")){
                    break;
                }
                p.displayEnterMessagePrompt();
                String content = scan.nextLine();
                replyMessage(content, recipients);
                break;

            case 3:
                viewEventList();
                break;

            case 4:
                viewSignedUpForEvent(this.username);
                break;

            case 5:
                Organizer temp = (Organizer) userManager.getUser(this.username);
                if(temp.getAttendingEvents().isEmpty()){
                    p.displayNotAttendingAnyEvents();
                    break;
                }
                viewSignedUpForEvent(this.username);
                p.displayEventCancelPrompt();
                // System.out.println("What is the name of the event you no longer want to attend? Type 'q' if you no longer want to cancel your spot in an event.");
                String cancel = scan.nextLine();
                if (cancel.equals("q")){
                    break;
                }
                if(!userManager.getAttendingEvents(this.username).contains(cancel)) {
                    p.displayEventCancelPrompt();
                    break;
                }
                else if(userManager.getAttendingEvents(this.username).size() == 0){
                    p.displayEventCancellationError2();
                    break;
                }
                cancelSpotInEvent(cancel);
                break;

            case 6:
                List<Event> future = viewFutureEventList();
                p.displayAllFutureEvents(future);
                if (future.size() == 0){
                    break;
                }
                p.displayEventSignUpPrompt();
                // System.out.println("What is the name of the event you would like to sign up for? Type 'q' if you would no longer like to sign up for an event.");
                String eventSignedUp = scan.nextLine();
                if (eventSignedUp.equals("q")){
                    break;
                }
                while (eventManager.getEvent(eventSignedUp) == null ||
                        !future.contains(eventManager.getEvent(eventSignedUp))){
                    p.displayInvalidEventSignUp();
                    eventSignedUp = scan.nextLine();
                    if (eventSignedUp.equalsIgnoreCase("q")){
                        break;
                    }
                }
                if (eventSignedUp.equalsIgnoreCase("q")){
                    break;
                }
                signUp(eventSignedUp);
                break;

            case 7:
                p.displayAddConferencePrompt();
                LocalDateTime time = askTime();
                while(!eventManager.between9to5(time) || !eventManager.checkTimeIsAfterNow(time)) {
                    if (!eventManager.between9to5(time)) {
                        p.displayInvalidHourError();
                        time = askTime();
                    } else if (!eventManager.checkTimeIsAfterNow(time)) {
                        p.displayInvalidDateError();
                        time = askTime();
                    }
                }
                p.displayEventTitlePrompt();
                String name = scan.nextLine();
                // Adding the option to end the case early here in case a User wants to go back
                if (name.equals("q")){
                    break;
                }
                p.displayEnterSpeakerPrompt();
                String speaker = scan.nextLine();
                if(!userManager.checkCredentials(speaker)) {
                    p.displaySpeakerCredentialError();
                    makeSpeaker();
                    p.displayEnterNewSpeakerPrompt();
                    speaker = scan.nextLine();
                }
                else{
                    while (!(userManager.getUserType(speaker) == "speaker")){
                        p.displayNotSpeakerError();
                        speaker = scan.nextLine();
                        if (speaker.equalsIgnoreCase("q")) {
                            break;
                        }
                    }
                }
                if (speaker.equalsIgnoreCase("q")) {
                    break;
                }
                p.displayEnterRoomNumberPrompt();
                int num = nextInt();

                boolean added = addEvent(name, speaker, time, num);

                if(!added) {p.displayEventCreationError();}
                else {
                    p.displaySuccessfulEventCreation();
                    userManager.addSpeakingEvent(speaker, name);
                    userManager.createdEvent(eventManager.getEvent(name), (Organizer) userManager.getUser(this.username));
                }
                break;

            case 8:
                p.displayAllAttendeeMessagePrompt();
                String message = scan.nextLine();
                if (message.equalsIgnoreCase("q")) {
                    break;
                }
                messageAllAttendees(message);
                p.displayMessageSentPrompt();
                break;

            case 9:
                p.displayEventMessagePrompt();
                String eventname = scan.nextLine();
                if (eventname.equalsIgnoreCase("q")) {
                    break;
                }
                else if(eventManager.getEvent(eventname) == null) {
                    p.displayInvalidEventError();
                } else {
                    p.displayAllAttendeeEventMessagePrompt();
                    messageEventAttendees(scan.nextLine(), eventname);
                    p.displayMessageSentPrompt();
                }
                break;

            case 10:
                p.displayAllSpeakerMessagePrompt();
                String speakermessage = scan.nextLine();
                if (speakermessage.equalsIgnoreCase("q")) {
                break;
                }
                messageAllSpeakers(speakermessage);
                p.displayMessageSentPrompt();
                break;

            case 11:
                List<String> eventNames = userManager.allCreatedEvents(this.username);
                List<Event> futureEvents = eventManager.chronologicalEvents(eventManager.eventNotHappened(eventNames));
                p.displayYourCreatedEvents(futureEvents);
                p.displayEventRemovalPrompt();
                String event = scan.nextLine();
                if (event.equalsIgnoreCase("q")) {
                    break;
                }
                while(!futureEvents.contains(eventManager.getEvent(event))){
                    p.displayCannotCancel();
                    event = scan.nextLine();
                    if (event.equalsIgnoreCase("q")) {
                        break;
                    }
                }
                if (event.equalsIgnoreCase("q")) {
                    break;
                }
                cancelEvent(event);
                break;

            case 12:
                p.displayEventReschedulePrompt();
                String eventname1 = scan.nextLine();
                if (eventname1.equalsIgnoreCase("q")) {
                    break;
                }
                else if(eventManager.getEvent(eventname1) == null) {
                    p.displayInvalidEventError();
                }
                LocalDateTime newTime = askTime();
                rescheduleEvent(eventname1, newTime);
                break;

            case 13:
                makeSpeaker();
                break;

            case 14:
                p.displayOptions2();
                break;


            case 15:
                p.displayRoomCreationPrompt();
                int roomNumber = nextInt();
                if (roomNumber == -1) {
                    break;
                }
                boolean roomAdded = eventManager.addRoom(roomNumber);
                if(!roomAdded) p.displayRoomAlreadyExists();

                break;

            case 16:
                p.displayRoomList(eventManager.getRooms());
                break;

            case 17:
                p.displayUserList(users("speaker"), "Speaker");
                break;

            case 18:
                p.displayUserList(users("attendee"), "Attendee");
                break;

            case 19:
                p.displayUserList(users("organizer"), "Organizer");
                break;

            default:
                p.displayInvalidInputError();
                break;
        }
        p.displayNextTaskPromptOrganizer();
    }




    /**
     * This method adds an event to the set of events in the conference
     * @param name This parameter refers to the name of the event.
     * @param speaker This parameter refers to the speaker at the event.
     * @param time This parameter refers to the event time.
     * @param roomNumber This parameter refers to the room number.
     * @return Returns true if the user was added to events map and false otherwise.
     */
    boolean addEvent(String name, String speaker, LocalDateTime time, int roomNumber) {
        return eventManager.addEvent(name, speaker, time, roomNumber);
    }

    /**
     * This method sends a message to all the attendees at the conference.
     * @param message This parameter is the message text
     */
    void messageAllAttendees(String message) {
        createBlastMessage("attendee", message);
    }

    /**
     * This method sends a message to all the attendees at a particular event.
     * @param message This parameter is the message text
     * @param name This parameter is the event name
     */
    void messageEventAttendees(String message, String name) {
        messageManager.speakerBlastMessage(Arrays.asList(name), message, eventManager, this.username);
    }

    /**
     * This method sends a message to all the speakers at the conference.
     * @param message This parameter is the message text
     */
    void messageAllSpeakers(String message) {
        createBlastMessage("speaker", message);
    }

    /**
     * This method cancels an event
     * @param name This is the title of the event to be canceled
     */
    void cancelEvent(String name) {
        eventManager.getAllEvents().remove(name);
    }

    /**
     * This method reschedules an event if the event is in the list of events and the new time is after the current time
     * @param name The name of the event
     * @param newTime The new time
     */
    void rescheduleEvent(String name, LocalDateTime newTime) {

        if(eventManager.getAllEvents().containsKey(name) && !newTime.isBefore(LocalDateTime.now())){
            eventManager.getAllEvents().get(name).setTime(newTime);
        }
    }

    /**
     * This method creates a speaker account
     * @param name This parameter refers to the name of the speaker.
     * @param address This parameter refers to the address of the speaker.
     * @param email This parameter refers to the email of the speaker.
     * @param username This parameter refers to the username of the speaker.
     * @param password This parameter refers to the password of the speaker.
     */
    void createSpeakerAccount(String name, String address, String email, String username, String password) {
        userManager.addUser(name, address, email, username, password, "speaker");
    }


    /**
     * This method sends messages to all people of a specific type
     * @param blastType This is the type of the individual. Choices are "speaker", "organizer" and "attendee"
     * @param message This is the actual message
     */
    void createBlastMessage(String blastType, String message) {

        Map<String, String> userTypes = userManager.getUserTypes();

        for(String user : userTypes.keySet()) {
            if(userTypes.get(user).equals(blastType)) {
                Message mssg = messageManager.createNewMessage(message, this.username, user);
                messageManager.addMessage(user, mssg);
            }
        }

    }

    private LocalDateTime getTime() throws DateTimeException {
        p.displayEnterYearPrompt();
        int y = nextInt();
        p.displayEnterMonthPrompt();
        int m = nextInt();
        p.displayEnterDayPrompt();
        int d = nextInt();
        p.displayEnterHourPrompt();
        int h = nextInt();
        p.displayEnterMinutePrompt();
        int mi = nextInt();
        return LocalDateTime.of(y, m, d, h, mi);
    }

    private LocalDateTime askTime() {
        LocalDateTime time = LocalDateTime.now();
        do {
            try {
                time = getTime();
                if(time.isBefore(LocalDateTime.now())){
                    p.displayInvalidEventError();
                }else{
                    break;
                }
            } catch (DateTimeException d) {
                p.displayDateError();
            }
        } while(true);

        return time;
    }

    private void makeSpeaker() {
        p.displayEnterUsernamePrompt();
        String username = scan.nextLine();
        while(this.userManager.checkCredentials(username) || (username.length() < 3 && username.equalsIgnoreCase("q"))){
            if (username.equalsIgnoreCase("q")) {
                break;
            } else if (this.userManager.checkCredentials(username)) {
                p.displayRepeatUsernameError();
            }
            else if (username.length() < 3) {
                p.displayUsernameLengthError();
            }
            username = scan.nextLine();
        }
        if (username.equalsIgnoreCase("q")) {
            return;
        }
        p.displayEnterPasswordPrompt();
        String password = scan.nextLine();
        while(password.length() < 3){
            p.displayPasswordLengthError();
            password = scan.nextLine();
        }
        p.displayEnterSpeakerNamePrompt();
        String name = scan.nextLine();
        while(name.length() < 2) {
            p.displaySpeakerNameError();
            name = scan.nextLine();
        }
        p.displayEnterSpeakerAddressPrompt();
        String address = scan.nextLine();
        while(address.length() < 6) {
            p.displayAddressLengthError();
            address = scan.nextLine();
        }
        p.displayEnterSpeakerEmailPrompt();
        String email = scan.nextLine();
        Pattern email_pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        while(!email_pattern.matcher(email).matches()){
            p.displayInvalidEmail();
            email = scan.nextLine();
        }
        createSpeakerAccount(name, address, email, username, password);
        messageManager.addUserInbox(username);
    }

    private List<String> getSenders(String username){
        List<Message> allMessages = messageManager.viewMessages(username);
        List<String> users = new ArrayList<>();
        for (Message message: allMessages){
            users.add(messageManager.getSender(message));
        }
        return users;
    }


    private List<User> users(String type) {
        return userManager.getUserMap().values().stream()
                .filter(user -> user.getUserType().equals(type))
                .collect(Collectors.toList());
    }
}
