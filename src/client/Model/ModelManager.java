package client.Model;

import Shared.Employee.Employee;
import Shared.Employee.EmployeeList;
import Shared.Event.Event;
import Shared.Event.EventList;
import Shared.Messages.Message;
import Shared.Messages.MessageRoom;
import Shared.Messages.MessageRoomList;
import Shared.Room.Room;
import Shared.Room.RoomList;
import client.RmiClient;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;


//TODO Add database updating

public class ModelManager implements Model
{

    private EmployeeList employeeList;
    private Event event;
    private EventList eventList;
    private RoomList roomList;
    private MessageRoomList messageRoomList;
    private ArrayList<Integer> idT;
    private ArrayList<Employee> employeesT;
    private Model model;

    private Employee loggedEmployee;

    private RmiClient api;

    public ModelManager(RmiClient client)
    {
        this.api = client;

        this.event = new Event();
        this.eventList = new EventList();
        this.roomList = new RoomList();
        this.employeeList = new EmployeeList();
        this.messageRoomList = new MessageRoomList();
        this.idT = new ArrayList<>();
        this.employeesT = new ArrayList<>();

        employeeList.setMessageRoomList(messageRoomList);
        employeeList.setEventList(eventList);
        messageRoomList.setEmployeeList(employeeList);


        //todo REMOVE THIS

        /*employeeList.addEmployee("Adam123", "Adam", "Halama", "CFO");
        employeeList.addEmployee("Klaudi123", "klaudi", "Var", "CBO");


        messageRoomList.addMessageRoom("Test room t1");
        messageRoomList.getMessageRoomByID(1).addUser(1);
        messageRoomList.getMessageRoomByID(1).addMessage(new Message(1, (System.currentTimeMillis()) , "Helooooo"));


        messageRoomList.addPrivateMessageRoom("Private message room t2", 1, 2);*/

        /*try
        {
            System.out.println(api.registerEmployee("admin", "admin", "Admin", "Admin", "Admin"));
        } catch (GeneralSecurityException | IOException | SQLException e)
        {
            e.printStackTrace();
        }*/

//        loggedClientID = 1;

        //logging in works, but i dont want to go to the login view every time


//        loggedEmployee = new Employee(7, "admin", "Admin", "Admin", "Admin");
//        employeeList.addEmployee(loggedEmployee);


        try
        {
            this.login("admin", "admin");
        } catch (SQLException | GeneralSecurityException | IOException throwables)
        {
            throwables.printStackTrace();
        }

    }

    @Override
    public void login(String username, String password) throws SQLException, GeneralSecurityException, IOException
    {
        loggedEmployee = api.loginEmployee(username, password);

        employeeList.addEmployee(loggedEmployee);
    }

    @Override
    public void logOut()
    {
        loggedEmployee = null;
    }


    @Override
    public int getLoggedClientID()
    {
        if (loggedEmployee == null)
            return 0;
        return loggedEmployee.getId();
    }

    @Override
    public Employee getLoggedEmployee()
    {
        return loggedEmployee;
    }

    @Override
    public void addMessageRoom(String name)
    {
        messageRoomList.addMessageRoom(name);
    }

    @Override
    public MessageRoom createPrivateMessageRoom(int employeeID1, int employeeID2) throws SQLException, RemoteException
    {
        return api.createPrivateMessageRoom(employeeID1, employeeID2);
    }

    @Override
    public void addMessageRoom(String name, ArrayList<Integer> usersIDs)
    {
        messageRoomList.addMessageRoom(name, usersIDs);
    }

    @Override
    public void removeMessageRoomFromEmployee(int messageRoomID)
    {
        messageRoomList.removeMessageRoom(messageRoomID);
    }

    @Override
    public void removeMessageRoomFromEmployee(MessageRoom room)
    {
        messageRoomList.removeMessageRoom(room);
    }

    @Override
    public ArrayList<MessageRoom> getMessageRoomsByEmployeeID(int employeeID)
    {
        return messageRoomList.getMessageRoomsByEmployeeID(employeeID);
    }

    @Override
    public ArrayList<MessageRoom> getMessageRoomsByAnything(String keyword)
    {
        return messageRoomList.getMessageRoomsByAnything(keyword);
    }

    @Override
    public ArrayList<MessageRoom> getMessageRooms()
    {
        return messageRoomList.getMessageRooms();
    }

    @Override
    public String getSenderAndBody(Message message)
    {
        if (message == null)
            return "";
        return employeeList.getEmployeeByID(message.getUserID()).getFullName() + ": " + message.getMessage();
    }

    @Override
    public ArrayList<String> getMessageRoomParticipantNames(MessageRoom messageRoom)
    {
        ArrayList<String> participants = new ArrayList<>();

        for (int userID :
                messageRoom.getUsersIDs())
        {
            participants.add(employeeList.getEmployeeByID(userID).getName());
        }

        return participants;
    }

    @Override
    public MessageRoom getMessageRoomByID(int id) throws SQLException, RemoteException
    {
        return api.getMessageRoomByID(id);
    }


    @Override
    public void addEmployee(Employee employee)
    {
        employeeList.addEmployee(employee);
        //TODO probably delete the whole method
    }

    @Override
    public void addEmployee(String username, String password, String name, String surname, String role) throws SQLException, GeneralSecurityException, IOException
    {
        employeeList.addEmployee(api.registerEmployee(username, password, name, surname, role));
    }

    @Override
    public void addEmployee(String username, String password, String name, String surname,
                            String role, ArrayList<String> permissions) throws SQLException, GeneralSecurityException, IOException
    {
        Employee employee = api.registerEmployee(username, password, name, surname, role);
        employeeList.addEmployee(employee);
        for (String permission :
                permissions)
        {
            api.addPermission(getLoggedClientID(), employee.getId(), permission);
        }
    }

    @Override
    public void removeEmployee(int employeeID)
    {
        employeeList.removeEmployee(employeeID);
    }

    @Override
    public ArrayList<Employee> getEmployees()
    {
        return employeeList.getEmployees();
    }

    @Override
    public ArrayList<Employee> getEmployees(ArrayList<Integer> employeesIDs) throws RemoteException
    {
        return api.getEmployees(employeesIDs);
    }

    @Override
    public ArrayList<Employee> getEmployeesByMessageRoom(int messageRoom)
    {
        return employeeList.getEmployeesByMessageRoom(messageRoom);
    }

    @Override
    public ArrayList<Employee> getEmployeesByEvent(int eventID)
    {
        return employeeList.getEmployeesByEvent(eventID);
    }

    @Override
    public ArrayList<Employee> getEmployeesByRole(String role)
    {
        return employeeList.getEmployeesByRole(role);
    }

    @Override
    public ArrayList<Employee> getEmployeesByText(String text)
    {
        return employeeList.getEmployeesByText(text);
    }

    @Override
    public ArrayList<Employee> getEmployeesByAnything(String keyword)
    {
        return employeeList.getEmployeesByAnything(keyword);
    }

    @Override
    public Employee getEmployeeByID(int ID) throws SQLException, RemoteException
    {
        return api.getEmployeeByID(ID);
    }

    @Override
    public Employee employeeSetName(int employeeID2, String name) throws SQLException, RemoteException
    {
        return api.employeeSetName(getLoggedClientID(), employeeID2, name);
    }

    @Override
    public Employee employeeSetSurname(int employeeID2, String surname) throws SQLException, RemoteException
    {
        return api.employeeSetSurname(getLoggedClientID(), employeeID2, surname);
    }

    @Override
    public Employee employeeSetRole(int employeeID2, String role) throws SQLException, RemoteException
    {
        return api.employeeSetRole(getLoggedClientID(), employeeID2, role);
    }

    /**
     * Removes the employee from an event.
     * @param employeeID An integer containing the Employee's ID
     * @param eventID An integer containing the event ID.
     */
    @Override
    public void removeEventFromEmployee(int employeeID, int eventID) throws SQLException, RemoteException
    {
        Employee employeeByID = getEmployeeByID(employeeID);
        ArrayList<Integer> eventIDs = employeeByID.getEvents();

        for (int i = 0; i < eventIDs.size(); i++)
        {
            if (eventIDs.get(i) == eventID)
            {
                employeeByID.removeEvent(i);
                return;
            }
        }

    }

    /**
     * Removes the employee from a message room.
     * @param employeeID An integer containing the Employee's ID
     * @param messageRoomID An integer containing the message room's ID.
     */
    @Override
    public void removeMessageRoomFromEmployee(int employeeID, int messageRoomID) throws SQLException, RemoteException
    {
        Employee employeeByID = getEmployeeByID(employeeID);
        ArrayList<Integer> messageRoomIDs = employeeByID.getMessageRooms();

        for (int i = 0; i < messageRoomIDs.size(); i++)
        {
            if (messageRoomIDs.get(i) == messageRoomID)
            {
                employeeByID.removeMessageRoom(i);
                return;
            }
        }

    }

    @Override
    public void addRoom(String roomNumber, String buildingAddress, int numberOfSeats, int floor) throws SQLException, RemoteException
    {
        Room room = api.createRoom(getLoggedClientID(), roomNumber, buildingAddress, numberOfSeats, floor);
        roomList.addRoom(room);
        System.out.println(roomNumber);
    }

    @Override
    public void addRoom(String roomCode, String buildingAddress, int numberOfSeats, int floor, ArrayList<String> equipment)
    {
        roomList.addRoom(roomCode, buildingAddress, numberOfSeats, floor, equipment);
        //TODO add adding rooms with equipment
    }

    @Override
    public void removeRoom(int roomID)
    {
        roomList.removeRoom(roomID);
    }

    @Override
    public void removeRoom(Room room)
    {
        roomList.removeRoom(room);
    }

    @Override
    public void modifyRoom(String roomID, String roomCode, String buildingAddress, int numberOfSeats, int floor, ArrayList<String> equipment)
    {
        roomList.modifyRoom(roomID, roomCode, buildingAddress, numberOfSeats, floor, equipment);
    }

    @Override
    public void modifyRoom(Room room, String roomCode, String buildingAddress, int numberOfSeats, int floor, ArrayList<String> equipment)
    {
        roomList.modifyRoom(room, roomCode, buildingAddress, numberOfSeats, floor, equipment);
    }

    @Override
    public int getRoomsCreated()
    {
        return RoomList.getRoomsCreated();
    }

    @Override
    public ArrayList<Room> getRooms()
    {
        return roomList.getRooms();
    }

    @Override
    public ArrayList<Room> getRoomsByAnything(String keyword)
    {
        return roomList.getRoomsByAnything(keyword);
    }

    @Override
    public Room getRoomByID(int roomID) throws SQLException, RemoteException
    {
        Room room = api.getRoomByID(roomID);
        roomList.addRoom(room);
        return room;
    }

    @Override
    public void removeEquipment(Room room, String removedEquipment)
    {
        room.removeEquipment(removedEquipment);
    }

    @Override
    public void addEquipment(Room room, String addedEquipment)
    {
        room.addEquipment(addedEquipment);
    }

    @Override
    public void setBuildingAddress(Room room, String buildingAddress)
    {
        room.setBuildingAddress(buildingAddress);
    }

    @Override
    public void setEquipment(Room room, ArrayList<String> equipment)
    {
        room.setEquipment(equipment);
    }

    @Override
    public void setFloor(Room room, int floor)
    {
        room.setFloor(floor);
    }

    @Override
    public void setNumberOfSeats(Room room, int numberOfSeats)
    {
        room.setNumberOfSeats(numberOfSeats);
    }

    @Override
    public void setRoomNumber(Room room, String roomNumber)
    {
        room.setRoomNumber(roomNumber);
    }

    @Override
    public void setTitle(String title)
    {
        event.setTitle(title);
    }

    @Override
    public void setParticipants(ArrayList<Integer> employees)
    {
        event.setParticipants(employees);
    }

    @Override
    public ArrayList<Integer> getParticipants()
    {
        return event.getParticipants();
    }

    @Override
    public void setOnline(boolean isOnline)
    {
        event.setOnline(isOnline);
    }

    @Override
    public void setRoom(int room)
    {
        event.setRoom(room);
    }

    @Override
    public void setPlatform(String platform)
    {
        event.setPlatform(platform);
    }

    @Override
    public int getEvent_id()
    {
        return event.getEvent_id();
    }

    @Override
    public String getTitle()
    {
        return event.getTitle();
    }

    @Override
    public String getPlatform()
    {
        return event.getPlatform();
    }

    @Override
    public int getRoomID()
    {
        return event.getRoomID();
    }

    @Override
    public boolean isOnline()
    {
        return event.isOnline();
    }

    @Override
    public long getStartTime() {
        return event.getStartTime();
    }

    @Override
    public long getEndTime() {
        return event.getEndTime();
    }

    @Override
    public long getCreateTime() {
        return event.getCreateTime();
    }

    @Override
    public void add(Event event) throws IllegalArgumentException
    {
        eventList.add(event);
    }

    @Override
    public ArrayList<Event> getEvents()
    {
        return eventList.getEvents();
    }

    @Override
    public ArrayList<Event> getEventByAnything(String s, String d)
    {
        return eventList.getEventByAnything(s, d);
    }

    @Override
    public ArrayList<Event> getEventExceptDate(String s)
    {
        return eventList.getEventExceptDate(s);
    }

    @Override
    public ArrayList<Event> getEventOnlyDate(String date)
    {
        return eventList.getEventOnlyDate(date);
    }

    @Override
    public Event getEventByIndex(int index)
    {
        return eventList.getEventByIndex(index);
    }

    @Override
    public Event getEventByID(int id)
    {
        return eventList.getEventByID(id);
    }

    @Override
    public void remove(int index)
    {
        eventList.remove(index);
    }

    @Override
    public void removeByEventID(int id)
    {
        eventList.removeByEventID(id);
    }

    @Override
    public void removeAllEvents()
    {
        eventList.removeAll();
    }

    @Override
    public int getSize()
    {
        return eventList.getSize();
    }

    @Override
    public ArrayList<Integer> getParticipantsIDT()
    {
        return idT;
    }

    @Override
    public ArrayList<Employee> getParticipantsT()
    {
        return employeesT;
    }

    @Override
    public void addIDT(int id)
    {
        idT.add(id);
        addEmployeeT(id);
    }

    @Override
    public void addEmployeeT(int id)
    {
        employeesT.add(employeeList.getEmployeeByID(id));
    }

    @Override
    public void removeEmployeeT(int id)
    {
        for (int i = 0; i < employeesT.size(); i++)
        {
            if (employeesT.get(i).getId() == id)
            {
                employeesT.remove(i);
            }
        }
    }

    @Override
    public void clearTemporary()
    {
        employeesT.clear();
        idT.clear();
    }

}
