import java.util.*;

public class MainInterface {

    public static void main(String[] args) {
        Database db = new Database("UserDatabase.txt");
        Scanner scan = new Scanner(System.in);
        boolean loop1 = true;
        boolean loop2 = false;
        while (loop1) {

            String PROMPT = "Would you like to..." +
                    "\n1. Login" +
                    "\n2. Create an account" +
                    "\n3. Exit";


            //Welcome message
            System.out.println("Welcome to the Turkey Store!");
            System.out.println(PROMPT);
            int resp = -1;
            do {
                try {
                    resp = Integer.parseInt(scan.nextLine());
                    if (resp < 1 || resp > 3) {
                        System.out.println("Input invalid, try again.");
                        System.out.println(PROMPT);
                        resp = -1;
                    }
                } catch (Exception e) {
                    System.out.println("Input invalid, try again.");
                    System.out.println(PROMPT);
                    resp = -1;
                }
            } while (resp == -1);

            MessageManager messageManager = new MessageManager(db, "history");
            if (resp == 1) { //Login
                boolean loggedIn = false;
                String email = "";
                String password = "";
                while (!loggedIn) {
                    System.out.println("Please enter your email: ");
                    email = scan.nextLine();
                    System.out.println("Please enter your password: ");
                    password = scan.nextLine();
                    if (db.verify(email, password)) {
                        loggedIn = true;
                        loop2 = true;
                        System.out.println("Login successful!");
                        HashMap<String, String> acct = db.get("email", email);
                        User user = new User(email, password, acct.get("role"), messageManager, db);
                        while (loop2) {
                            String MESSAGEPROMPT = "Would you like to..." +
                                    "\n1. Message a user" +
                                    "\n2. View messages" +
                                    "\n3. Edit a message" +
                                    "\n4. Delete a message" +
                                    "\n5. Export conversations" +
                                    "\n6. Block a user" +
                                    "\n7. Unblock a user" +
                                    "\n8. View stores" +
                                    "\n9. View customers" +
                                    "\n10. Add a store" +
                                    "\n11. Dashboard" +
                                    "\n12. Open Filter" +
                                    "\n13. Delete your account" +
                                    "\n14. Exit";
                            System.out.println(MESSAGEPROMPT);
                            int userAction = -1;
                            do {
                                try {
                                    userAction = Integer.parseInt(scan.nextLine());
                                } catch (NumberFormatException e) {
                                    userAction = -1;
                                }
                                if (userAction < 1 || userAction > 14) {
                                    System.out.println("Input invalid, try again.");
                                    System.out.println(MESSAGEPROMPT);
                                }
                            } while (userAction < 1 || userAction > 14);
                            if (userAction == 1) {
                                //message user
                                if (acct.get("role").toLowerCase() == "customer") {
                                    //option to view available stores
                                    System.out.println("Would you like to view a list of the available stores? (Y/N)");
                                    String storeList = "";
                                    storeList = scan.nextLine().toUpperCase();
                                    while (!storeList.equals("Y") && !storeList.equals("N")) {
                                        System.out.println("Invalid input, try again.");
                                        System.out.println("Would you like to view a list of the available stores? (Y/N)");
                                        storeList = scan.nextLine().toUpperCase();
                                    }
                                    if (storeList.equals("Y")) {
                                        user.viewStores();
                                    }
                                } else {
                                    //option to view list of customers
                                    System.out.println("Would you like to view a list of customers? (Y/N)");
                                    String storeList = "";
                                    storeList = scan.nextLine().toUpperCase();
                                    while (!storeList.equals("Y") && !storeList.equals("N")) {
                                        System.out.println("Invalid input, try again.");
                                        System.out.println("Would you like to view a list of customers? (Y/N)");
                                        storeList = scan.nextLine().toUpperCase();
                                    }
                                    if (storeList.equals("Y")) {
                                        user.viewCustomers();
                                    }
                                }
                                MessageInterface.message(scan, messageManager, db, acct.get("id"));
                            } else if (userAction == 2){
                                MessageInterface.viewMessageHistory(scan, acct.get("id"), db, messageManager);
                            } else if (userAction == 3) {
                                //edit message
                                String view = "";
                                System.out.println("Would you like to see your message history to find the conversation? (Y/N)");
                                view = scan.nextLine().toUpperCase();
                                while (!view.equals("Y") && !view.equals("N")) {
                                    System.out.println("Invalid input, try again.");
                                    System.out.println("Would you like to see your message history to find the conversation? (Y/N)");
                                    view = scan.nextLine().toUpperCase();
                                }
                                if (view.equals("Y")) {
                                    MessageInterface.viewMessageHistory(scan, acct.get("id"), db, messageManager);
                                }
                                String recipient = "";
                                //Should this be changed to somehow take seller name and get convo?
                                //message recipient email
                                System.out.println("Who did you send the message to?");
                                recipient = scan.nextLine();
                                int messageNum;
                                //message number
                                System.out.println("Which message (number) would you like to edit?");
                                messageNum = scan.nextInt();
                                scan.nextLine();
                                String newMessage = "";
                                //what the new message will be
                                System.out.println("What would you like to change the message to?");
                                newMessage = scan.nextLine();
                                messageManager.editMessage(acct.get("ID"), db.get("role", recipient).get("email"), newMessage, String.valueOf(messageNum));
                            } else if (userAction == 4) {
                                //delete message
                                String view = "";
                                //option to view message history
                                System.out.println("Would you like to see your message history to find the conversation? (Y/N)");
                                view = scan.nextLine().toUpperCase();
                                while (!view.equals("Y") && !view.equals("N")) {
                                    System.out.println("Invalid input, try again.");
                                    System.out.println("Would you like to see your message history to find the conversation? (Y/N)");
                                    view = scan.nextLine().toUpperCase();
                                }
                                if (view.equals("Y")) {
                                    MessageInterface.viewMessageHistory(scan, acct.get("id"), db, messageManager);
                                }
                                String recipient = "";
                                //message recipient email
                                System.out.println("Who did you send the message to?");
                                recipient = scan.nextLine();
                                int messageNum;
                                //message number
                                System.out.println("Which message (number) would you like to delete?");
                                messageNum = scan.nextInt();
                                scan.nextLine();
                                messageManager.deleteMessage(acct.get("ID"), db.get("role", recipient).get("email"), String.valueOf(messageNum));
                            } else if (userAction == 5) {
                                //TODO: export conversations
                                MessageInterface.exportConversations(scan, acct.get("id"), db, messageManager);
                            } else if (userAction == 6) {
                                //block user
                                //option to see list of customers or stores (dep on role) to see who to block
                                if (acct.get("role").toLowerCase() == "customer") {
                                    System.out.println("Would you like to view a list of the available stores? (Y/N)");
                                    String storeList = "";
                                    storeList = scan.nextLine().toUpperCase();
                                    while (!storeList.equals("Y") && !storeList.equals("N")) {
                                        System.out.println("Invalid input, try again.");
                                        System.out.println("Would you like to view a list of the available stores? (Y/N)");
                                        storeList = scan.nextLine().toUpperCase();
                                    }
                                    if (storeList.equals("Y")) {
                                        user.viewStores();
                                    }
                                } else {
                                    System.out.println("Would you like to view a list of customers? (Y/N)");
                                    String storeList = "";
                                    storeList = scan.nextLine().toUpperCase();
                                    while (!storeList.equals("Y") && !storeList.equals("N")) {
                                        System.out.println("Invalid input, try again.");
                                        System.out.println("Would you like to view a list of customers? (Y/N)");
                                        storeList = scan.nextLine().toUpperCase();
                                    }
                                    if (storeList.equals("Y")) {
                                        user.viewCustomers();
                                    }
                                }
                                System.out.println("Who would you like to block?");
                                String userToBlock = "";
                                userToBlock = scan.nextLine();
                                if (db.get("email", userToBlock) == null) {
                                    System.out.println("That is not a valid email");
                                    continue;
                                }
                                System.out.println("Would you like to block them or become invisble to them (block/invisible)?");
                                boolean invisible = scan.nextLine().toLowerCase().equals("invisible");
                                try {
                                    db.block(email, userToBlock, invisible);
                                } catch (InvalidUserException e) {
                                    // TODO Auto-generated catch block
                                    System.out.println(e.getMessage());
                                    continue;
                                }
                                if (invisible) {
                                    System.out.println("You are invisible to " + userToBlock);
                                } else {
                                    System.out.println(userToBlock + " has been blocked!");
                                }
                            } else if (userAction == 7) {
                                //TODO: unblock a user
                                if (acct.get("role").equals(Role.Customer.toString())) {
                                    user.viewStores();
                                } else {
                                    user.viewCustomers();
                                }
                                System.out.println("Who would you like to unblock or become visible to?");
                                String name = scan.nextLine();
                                if (db.get("email", name) == null) {
                                    System.out.println("That is not a valid email");
                                    continue;
                                }
                                System.out.println("Would you like to unblock them or become visible to them (unblock/visible)");
                                boolean visible = scan.nextLine().toLowerCase().equals("visible");
                                try {
                                    db.unblock(email, name, visible);
                                } catch (InvalidUserException e) {
                                    // TODO Auto-generated catch block
                                    System.out.println(e.getMessage());
                                    continue;
                                }
                                if (visible) {
                                    System.out.println("You are visible to " + name);
                                } else {
                                    System.out.println(name + " has been unblocked!");
                                }
                            } else if (userAction == 8) {
                                //TODO: view stores
                                if (acct.get("role").equals(Role.Customer.toString())) {
                                    user.viewStores();
                                } else {
                                    System.out.println("You cannot do this, you are a seller!");
                                }
                            } else if (userAction == 9) {
                                //TODO: view customers
                                if (acct.get("role").equals(Role.Seller.toString())) {
                                    user.viewCustomers();
                                } else {
                                    System.out.println("You cannot do this, you are a customer!");
                                }
                            } else if (userAction == 10) {
                                if (acct.get("role").toLowerCase().equals("customer")) {
                                    System.out.println("You cannot do this, you are a customer!");
                                } else {
                                    String storeName = "";
                                    System.out.println("What is the name of the store you would like to add?");
                                    storeName = scan.nextLine();
                                    try {
                                        user.addStores(storeName);
                                    } catch (InvalidUserException e) {
                                        // TODO Auto-generated catch block
                                        System.out.println(e.getMessage());
                                        continue;
                                    }
                                    System.out.println(storeName + " has been created!");
                                }
                            } else if (userAction == 11) {
                                Dashboard dashboard = new Dashboard(email, "");
                                dashboard.readDatabase();
                                dashboard.presentDashboard(scan);
                            } else if (userAction == 12) {
                                //TODO: Not sure if we can keep this feature
                                Filter f = new Filter(email);
                                f.presentFilterMenu(scan);
                            } else if (userAction == 13) {
                                System.out.println("Are you sure you want to delete your account (Y/N)");
                                if (scan.nextLine().toUpperCase().equals("Y")) {
                                    System.out.println("Confirm by entering your password:");
                                    if (db.verify(email, password)) {
                                        try {
                                            db.remove(email);
                                        } catch (InvalidUserException e) {
                                            // TODO Auto-generated catch block
                                            System.out.println(e.getMessage());
                                            continue;
                                        }
                                        System.out.println("Account deleted!");
                                        loop2 = false;
                                    }
                                }
                            } else if (userAction == 14) {
                                //Exit? (Could be a go back to start button, would need while around everything then)
                                loop2 = false;
                            } else {
                                System.out.println("Incorrect username or password. Please try again");
                            }
                        }
                    } else {
                        System.out.println("Sorry, the email or password you entered is incorrect");
                    }
                }
            } else if (resp == 2) { //Creating Acct
                String email;
                do {
                    System.out.println("Please enter your email: ");
                    email = scan.nextLine();
                    if (!db.validate(email, "email")) {
                        System.out.println("Please enter a valid email");
                    }
                } while (!db.validate(email, "email"));
                String password;
                do {
                    System.out.println("Please enter your password: ");
                    password = scan.nextLine();
                    if (!db.validate(password, "password")) {
                        System.out.println("That password is not valid. Please do not use special characters");
                    }
                } while (!db.validate(password, "password"));
                boolean roleValid = false;
                String role = "";
                while (!roleValid) {
                    System.out.println("Please enter your role (Seller/Customer): ");
                    role = scan.nextLine();

                    if (role.toLowerCase().equals("seller") || role.toLowerCase().equals("customer")) {
                        roleValid = true;
                    }
                }
                try {
                    db.add(email, password, Role.valueOf(role));
                } catch (InvalidUserException e) {
                    // TODO Auto-generated catch block
                    System.out.println(e.getMessage());
                    continue;
                }
                User account = new User(email, password, role, messageManager, db);
                System.out.println("Account created!");
                if (role.toLowerCase().equals("seller")) {
                    System.out.println("How many stores would you like to create?");
                    int numStores = scan.nextInt();
                    for (int i = 0; i < numStores; i++) {
                        String storeName = "";
                        System.out.println("What is the name of store " + i + "?");
                        storeName = scan.nextLine();
                        try {
                            account.addStores(storeName);
                        } catch (InvalidUserException e) {
                            // TODO Auto-generated catch block
                            System.out.println(e.getMessage());
                            continue;
                        }
                        System.out.println(storeName + " has been created!");
                    }
                }
            } else if (resp == 3) {
                //Exit program
                //should this print at the end no matter what? if so, change to separate if loop at end
                System.out.println("Have a nice rest of your day!");
                loop1 = false;
            }
        }
    }
}