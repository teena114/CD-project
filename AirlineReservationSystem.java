import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.*;
import javax.swing.table.*;

public class AirlineReservationSystem {

    // Connection to the mySQL database
    static DatabaseConnection connect = new DatabaseConnection(
            "jdbc:mysql://localhost/passdb", "com.mysql.jdbc.Driver", "root",
            "teena");
    static private String seatID = "";
    static private int compareExp = 0;

    // static ArrayList<Integer> ids = new ArrayList<Integer>();

    public static void main(String[] args) throws SQLException {


        //opens main menu, determines whether user is regular user or admin
        mainMenu();



    }// main

    /**			mainMenu
     * main menu for the application
     *
     * - user selects between user/admin/exit button
     *
     */
    public static void mainMenu()
    {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(
                "                                                                          User or Admin?                                                                         ");

        //button declaration
        JButton userButton = new JButton("User");
        JButton adminButton = new JButton("Admin");
        JButton closeButton = new JButton("Exit");

        //frame declaration, initialization
        final JFrame frame = new JFrame();
        frame.setTitle("Airline Reservation System");
        frame.setBounds(100, 100, 500, 200);


        //User clicked
        userButton.addActionListener(new ActionListener() {
            //close frame, open user menu
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                userMenu();
            }
        });

        //admin clicked
        adminButton.addActionListener(new ActionListener() {
            //close frame, open admin menu
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                adminMenu();
            }
        });

        //exit clicked
        closeButton.addActionListener(new ActionListener(){
            //close frame
            public void actionPerformed(ActionEvent e){
                frame.dispose();
            }
        });

        //add components to panel
        panel.add(label);
        panel.add(userButton);
        panel.add(adminButton);
        panel.add(closeButton);

        //create container
        Container con = frame.getContentPane();
        con.add(panel);


        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**			adminMenu
     * Menu for administrators
     *
     */
    public static void adminMenu(){
        JPanel panel = new JPanel();
        JLabel label = new JLabel(
                "                                                                          What database would you like to access?                                                                         ");

        //button declaration

        JButton planeButton = new JButton("Plane");
        JButton flightButton = new JButton("Flight");
        JButton passengerButton = new JButton("Passenger");
        JButton seatButton = new JButton("Seat");

        //button actionlisteners


        planeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                //adminPlanes();
                PlaneAdmin pa = new PlaneAdmin(connect);
                pa.admin();

            }
        });

        flightButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                //adminFlights();
                FlightAdmin fa = new FlightAdmin(connect);
                fa.admin();

            }
        });

        passengerButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                //adminPassengers();
                PassengerAdmin pa = new PassengerAdmin(connect);
                pa.admin();

            }
        });

        seatButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                //adminSeats();
                SeatAdmin sa = new SeatAdmin(connect);
                sa.admin();
            }
        });

        //frame declaration, initialization
        final JFrame frame = new JFrame();
        frame.setTitle("Airline Reservation System");
        frame.setBounds(100, 100, 500, 200);




        //add components to panel
        panel.add(label);

        panel.add(planeButton);
        panel.add(flightButton);
        panel.add(passengerButton);
        panel.add(seatButton);

        //create container
        Container con = frame.getContentPane();
        con.add(panel);


        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    /**
     * getFlights
     *
     * @return all current flights in ResultSet form
     */
    public static ResultSet getFlights() {
        String query = "SELECT * FROM Flight";
        return connect.execute(query);
    }

    /**
     * getFlightDepTimes
     *
     * @return all flight departure times in ResultSet form
     */
    public static ResultSet getFlightDepTimes() {
        String query = "SELECT depTime FROM Flight";
        return connect.execute(query);
    }

    /**
     * getSeats
     *
     * Gets all available(non-taken) seats from a plane
     *
     * @param s
     *            - the planeID to get the seats from
     * @return available seats in ResultSet form
     */
    public static ResultSet getSeats(String s) {
        String query = "SELECT * FROM Seat WHERE taken = false AND planeID ="
                + s;
        return connect.execute(query);
    }

    /**
     * getRowNum get number of rows in a result set
     *
     * @param rs
     *            - ResultSet to operate on
     * @return number of rows
     */
    public static int getRowNum(ResultSet rs) {
        int num = 0;

        try {
            rs.last();
            num = rs.getRow();
            rs.beforeFirst();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return num;

    }

    /**
     * editFlight Performs process of booking a flight
     *
     * @param b - whether the action is to view or book a flight
     * @throws SQLException
     *
     */
    public static void editFlight(boolean b) throws SQLException {

        final JFrame frame = new JFrame();
        final boolean isbook  = b;
        ResultSet rs;
        frame.setTitle("Flight Table");
        frame.setBounds(300, 100, 800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        JPanel panel = new JPanel();
        frame.add(panel);
        JLabel label = new JLabel("Please select a flight");
        panel.add(label);
        String[] columnNames = { "Flight ID", "Destination", "Departure Date",
                "Departure Time", "Gate" };

        // get Flights
        rs = getFlights();

        // get number of rows
        int rowNum = getRowNum(rs);

        int i = 0;
        Object[][] data = new Object[rowNum][rowNum];
        while (rs.next()) {

            data[i][0] = rs.getInt("flightID");
            data[i][1] = rs.getString("destination");
            data[i][2] = rs.getDate("depDate");
            data[i][3] = rs.getTime("depTime");
            data[i][4] = rs.getInt("planeID");

            data[i][6] = rs.getString("gateID");

            i++;
        }

        final JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(750, 200));
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        // mouse click listen to select the flight. it will return flightID
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = table.getColumnModel().getColumnIndexAtX(
                        e.getX());
                int row = e.getY() / table.getRowHeight();
                Object value = "";
                if (row < table.getRowCount() && row >= 0
                        && column < table.getColumnCount())
                    value = table.getValueAt(row, 4);
                System.out.println(value);
                String planeID = value.toString();
                value = table.getValueAt(row, 0);
                String flightID = value.toString();

                try {

                    bookSeat(planeID, flightID, isbook);

                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                frame.dispose();
            }
        });
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
            }
        });
        panel.add(closeButton);
    }// editFlight

    /**			bookSeat
     * This is the menu to book a seat
     * @param planeID - ID of plane to book seat on
     * @param flightID - ID of flight to book seat for
     * @throws SQLException
     */
    public static void bookSeat(String planeID, final String flightID, boolean isbook)
            throws SQLException {
        final JFrame frame = new JFrame();
        frame.setBounds(300, 100, 500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        JPanel panel = new JPanel();
        frame.add(panel);
        JLabel label = new JLabel(
                "Please select a seat and enter your information. 'F' seats  = First class.");
        final JLabel nameLabel = new JLabel("Name: ");
        final JLabel ageLabel = new JLabel("Age: ");
        final JTextField nameField = new JTextField(20);
        final JTextField ageField = new JTextField(5);
        panel.add(label);

        // final String chosen = "";

        String[] columnNames = { "Seat ID", "Row", "Seat Number", };
        // sample data
        ResultSet rs = getSeats(planeID);

        int rowNum = getRowNum(rs);

        int i = 0;
        Object[][] data = new Object[rowNum][rowNum];
        while (rs.next()) {

            data[i][0] = rs.getString("sID");
            data[i][1] = rs.getString("rowN");
            data[i][2] = rs.getInt("seatNo");
            data[i][3] = rs.getBoolean("taken");
            data[i][4] = rs.getInt("planeID");

            i++;
        }

        final JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(450, 300));
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);



        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = table.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / table.getRowHeight();
                String value = "";
                if (row < table.getRowCount() && row >= 0
                        && column < table.getColumnCount())
                    value = (String) table.getValueAt(row, 0);
                seatID = value;

            }
        });
        if(isbook)
        {
            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(ageLabel);
            panel.add(ageField);
            JButton submitButton = new JButton("Submit");
            panel.add(submitButton);
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae)
                {
                    try {
                        String age = ageField.getText();
                        String name = nameField.getText();

                        System.out.println(name);
                        int pasNum = 0;
                        String pasID = "";
                        while(pasID.equals("") || isInDB(pasID))
                        {
                            while (pasNum < 99)
                                pasNum = (int) (Math.random()*1000);
                            pasID = pasNum  + "C";
                        }
                        if(!age.equals("") && !name.equals(""))
                        {
                            addSeatQuery(age, name, seatID, flightID, pasID);
                            JOptionPane.showMessageDialog(frame, "Thank you for using our system! Your passenger ID is " + pasID + ".");
                            frame.dispose();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(frame, "Please enter your information!");
                        }

                    }
                    catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
            }
        });
        panel.add(closeButton);
    }

    /*
     * checks if pasID is in Passenger table for insert and deletion
     */
    public static boolean isInDB(String pasID) throws SQLException
    {
        ResultSet rs;

        //default sql statement(if no attributes are specified
        String sql = "SELECT pasID FROM Passenger";
        rs = connect.execute(sql);
        while(rs.next())
        {
            if(pasID.equalsIgnoreCase(rs.getString("pasID")))
                return true;
        }
        return false;
    }

    /**			addSeatQuery
     *
     * Performs insertion of passenger to database
     * @param age - age of passenger
     * @param name - name of passenger
     * @param seatID - seat of passenger
     * @param flightID - flight of passenger
     */
    public static void addSeatQuery(String age, String name, String seatID,
                                    String flightID, String pasID) {
        boolean firstClass = false;

        if (seatID.charAt(2) == '7' || seatID.charAt(2) == '8')
            firstClass = true;

        String sql = "INSERT INTO Passenger(name, age, flightID, firstClass, seatID, pasID) "
                + "VALUES(\""
                + name
                + "\", "
                + age
                + ", "
                + flightID
                + ", "
                + firstClass
                + ", \'"
                + seatID
                + "\', \'"
                + pasID + "\')";

        try {
            connect.executeUpdate(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**          userMenu
     *
     * Creates the menu for a user
     */
    public static void userMenu() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(
                "                                       WELCOME TO  AIRLINE RESERVATION SYSTEM                                     ");

        JButton cancelRevButton = new JButton("Cancel Reservation");
        JButton selectFlightButton = new JButton("Book Flight");
        JButton viewButton = new JButton("View Flights");

        selectFlightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    editFlight(true);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        cancelRevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelRev();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    editFlight(false);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        panel.add(label);
        panel.add(selectFlightButton);
        panel.add(viewButton);

        panel.add(cancelRevButton);


        final JFrame frame = new JFrame();
        frame.setTitle("Airline Reservation System");
        frame.setBounds(100, 100, 500, 200);

        JButton closeButton = new JButton("Exit");
        closeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
            }
        });
        panel.add(closeButton);
        Container con = frame.getContentPane();
        con.add(panel);


        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }// user

    /**			cancelRev
     * This is the menu to cancel a reservation
     */
    public static void cancelRev() {
        final JFrame frame = new JFrame();
        frame.setTitle("Cancel Reservation");
        frame.setBounds(300, 100, 300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        JPanel panel = new JPanel();
        frame.add(panel);
        final JLabel text = new JLabel("Please enter your passenger ID");
        panel.add(text);

        final JTextField pasID = new JTextField(10);

        panel.add(pasID);
        JButton submitButton = new JButton("Submit");
        panel.add(submitButton);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    if(!pasID.getText().equals("") && isInDB(pasID.getText()))
                    {
                        cancelSeat(pasID.getText());
                        JOptionPane.showMessageDialog(frame, "Your reservation has been cancelled.");
                        frame.dispose();
                    }
                    else if(pasID.getText().equals(""))
                        JOptionPane.showMessageDialog(frame, "Please enter your passenger ID!");
                    else
                        JOptionPane.showMessageDialog(frame, "Your passenger ID is not in the system. Please make sure you enter the correct ID!");
                }
                catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
            }
        });
        panel.add(closeButton);
    }

    /**			cancelSeat
     * Cancels the seat reservation of a passenger(pasID)
     * @param pasID - ID of passenger to cancel seat of
     */
    public static void cancelSeat(String pasID) {

        String sql = "DELETE FROM Passenger WHERE pasID = \'" + pasID + "\'";
        // System.out.println(sql);
        try {
            connect.executeUpdate(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}