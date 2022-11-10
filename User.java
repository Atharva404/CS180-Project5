import java.io.*;
import java.util.ArrayList;

public class User {
    private String email;
    private String password;
    private String username;
    private String role;
    private Database db;
    private ArrayList<String> stores;
    private MessageManager manager;
    //creating an account
    public User(String username, String email, String password, String role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.manager = new MessageManager();
        db = new Database("UserDatabase.txt");
        stores = new ArrayList<>();
    }

    //deleting an account
    public void deleteAccount() {
        this.email = null;
        this.username = null;
        this.password = null;
        this.role = null;
    }

    //edit an account
    public void editAccount(String username, String email, String password, String role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void viewStores() throws InvalidUserException, IOException {
        //Will print store and seller
        if (this.role.toLowerCase().equals("customer")) {
            BufferedReader br = new BufferedReader(new FileReader("stores.txt"));
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
            }
        }
    }

    public void selectStore(String store, String message) throws InvalidUserException, IOException {
        if (this.role.toLowerCase().equals("customer")) {
            ArrayList<String> stores = new ArrayList<String>();
            BufferedReader br = new BufferedReader(new FileReader("stores.txt"));
            String line = br.readLine();
            while (line != null) {
                stores.add(line);
                line = br.readLine();
            }
            for (int i = 0; i < stores.size(); i++) {
                if (stores.get(i).contains(store)) {
                    String seller = stores.get(i).split("-")[1];
                    manager.messageUser(this.username, seller, message);
                }
            }
        }
    }

    //Implementing Seller
    public void addStores(String store) {
        if (this.role.toLowerCase().equals("seller")) {
            try {
                File f = new File("stores.txt");
                FileOutputStream fos = new FileOutputStream(f, false);
                PrintWriter pw = new PrintWriter(fos);
                pw.write(store + "-" + this.username + "\n");
                stores.add(store);
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void viewCustomers() throws InvalidUserException {
        if (this.role.toLowerCase().equals("seller")) {
            ArrayList<String> customers = manager.getNames("Customer");
            for (String customer : customers) {
                System.out.println(customer);
            }
        }
    }

    public ArrayList<String> getStores() {
        return stores;
    }

    public void selectCustomer(String recipient, String message) throws InvalidUserException, IOException {
        if (this.role.toLowerCase().equals("seller")) {
            ArrayList<String> customers = manager.getNames("Customer");
            for (String customer : customers) {
                if (username.equals(customer)) {
                    manager.messageUser(this.username, recipient, message);
                }
            }
        }
    }
}
