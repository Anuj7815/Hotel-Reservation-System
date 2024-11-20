import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.*;
public class HotelManagementSystem{
    private static  final String url="jdbc:mysql://localhost:3306/hoteldatabase";
    private static final String username="root";
    private static final String password="anuj";
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        // this is used to load the drivers.
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println("Unable to load drivers: "+e.getMessage());
        }

        //this code is used to connect the database
        try{
            Connection conn=DriverManager.getConnection(url,username,password);
            System.out.println("Database Connected Successfully");

            while(true){

                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc=new Scanner(System.in);
                System.out.println("1. Reserve a Room.");
                System.out.println("2. View Reservation.");
                System.out.println("3. Get Room Number.");
                System.out.println("4. Update Reservations.");
                System.out.println("5. Delete Reservations.");
                System.out.println("0. Exit.");

                System.out.println("Choose an Option: ");
                int choice=sc.nextInt();

                if(choice==1){
                    reserveRoom(conn,sc);
                }else if(choice==2){
                    viewReservations(conn);
                }else if(choice==3){
                    getRoomNumber(conn,sc);
                }else if(choice==4){
                    updateReservations(conn,sc);
                }else if(choice==5){
                    deleteReservations(conn,sc);
                }else if(choice==0){
                    exit();
                    sc.close();
                    break;
                }else{
                    System.out.println("Invalid choice. Please enter correct option.");
                }
            }
        }catch(SQLException e){
            System.out.println("Failed to Connect to database: "+e.getMessage());
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    //reserveRoom() method is used to provide new reservations
    private static void reserveRoom(Connection conn,Scanner sc){
        try{
            System.out.print("Enter Guest Name: ");
            String guestName=sc.next();
            sc.nextLine();
            System.out.print("Enter Room Number: ");
            int roomNumber=sc.nextInt();
            System.out.print("Enter Contact Number: ");
            String contactNumber=sc.next();

            String command="INSERT INTO RESERVATIONS (guest_name,room_number,contact_number)"+
                    "VALUES ('" +guestName+"',"+roomNumber+",'"+contactNumber+"')";

            try(Statement st=conn.createStatement()){
                int affectedRow=st.executeUpdate(command);
                if(affectedRow>0){
                    System.out.println("Reservation Successfull");
                }else{
                    System.out.println("Reservation Failed");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection conn) throws SQLException{
        String sql="SELECT reservation_id,guest_name,room_number,contact_number,reservation_date FROM RESERVATIONS";

        try(Statement st=conn.createStatement();
            ResultSet res=st.executeQuery(sql)){

            System.out.println("Current Reservation: ");
            System.out.println("************************");
            while(res.next()){
                int id=res.getInt("reservation_id");
                String name=res.getString("guest_name");
                int roomNumber=res.getInt("room_number");
                String contactNumber=res.getString("contact_number");
                String resDate=res.getTimestamp("reservation_date").toString();

                System.out.println("Reservation ID: "+id+"**** Guest Name: "+name+"**** Room Number: "+roomNumber +
                        "**** Contact Number: "+contactNumber+"**** Reservation Date: "+resDate);
            }
        }catch(SQLException e){
            System.out.println("Failed to Fetch current Reservation Data: "+e.getMessage());
        }
    }

    private static void getRoomNumber(Connection conn,Scanner sc){
        try{
            System.out.println("Enter reservation ID: ");
            int id=sc.nextInt();
            System.out.println("Enter Guest Name: ");
            String name=sc.next();

            String sql="SELECT room_number FROM RESERVATIONS "+
                    "WHERE reservation_id= "+id+
                    " AND guest_name='"+name+"'";

            try(Statement st=conn.createStatement();
            ResultSet res=st.executeQuery(sql)){
                if(res.next()){
                    int room=res.getInt("room_number");
                    System.out.println("Room Number is: "+room);
                }else{
                    System.out.println("Reservation is not found for the given ID and Room Number.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservations(Connection conn,Scanner sc){
        try{
            System.out.println("Enter Reservation ID to update: ");
            int reserveID=sc.nextInt();
            sc.nextLine();

            if(!reservationExists(conn,reserveID)){
                System.out.println("Reservation Not found for the given ID.");
                return;
            }

            System.out.println("Enter the guest Name to Update: ");
            String newName=sc.next();
            System.out.println("Enter new Room Number: ");
            int newRoomNumber=sc.nextInt();
            System.out.println("Enter New Contact Number: ");
            String newContactNumber=sc.next();

            String sql="UPDATE reservations SET guest_name='"+newName+"', "+
                    "room_number="+newRoomNumber+", "+
                    "contact_number='"+newContactNumber+"' "+
                    "WHERE reservation_id="+reserveID;

            try(Statement st=conn.createStatement()){
                int affectedRows=st.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("Reservation Updated Successfully.");
                }else{
                    System.out.println("Failed to Update the reservations.");
                }
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static  void deleteReservations(Connection conn,Scanner sc){
        try{
            System.out.println("Enter Reservation ID to delete: ");
            int id=sc.nextInt();

            if(!reservationExists(conn,id)){
                System.out.println("Reservation Not Found for the given ID.");
                return;
            }

            String sql="DELETE FROM reservations WHERE reservation_id="+id;

            try(Statement st=conn.createStatement()) {
                int affectedRows = st.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println(affectedRows + " Number of rows deleted successfully.");
                } else {
                    System.out.println("Failed to delete the rows from the table.");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection conn,int id){
        try{
            String sql="SELECT reservation_id FROM RESERVATIONS WHERE reservation_id="+id;
            try(Statement st=conn.createStatement();
                ResultSet res=st.executeQuery(sql)){
                    return res.next();
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Exiting System: ");
        int i=5;
        while(i>0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("Thank You for your reservations.");
    }
}