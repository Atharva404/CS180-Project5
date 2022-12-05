import java.util.*;
import java.io.*;
import java.net.*;
import java.time.Instant;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Server implements Runnable {
    
    private MessageManager mm;
    private Database db;
    private User user;
    private Dashboard dashboard;
    private Filter filter;
    private boolean loggedIn;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public Server(Socket socket, MessageManager mm, Database db) {
        this.mm = mm;
        this.db = db;
        this.dashboard = null;
        this.filter = null;
        this.loggedIn = false;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            run();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object input = ois.readObject();
                if (input == null) {
                    continue;
                }
                Query query = (Query) input;
                Object o = null;
                String function = query.getFunction();
                Object[] args = query.getArgs();
                if (query.getObject().equals("Database") && (function.equals("verify") || function.equals("add"))) {
                    o = db;
                } else if (loggedIn) {
                    if (query.getObject().equals("Database")) {
                        o = db;
                    } else if (query.getObject().equals("User")) {
                        o = user;
                    } else if (query.getObject().equals("MessageManager")) {
                        o = mm;
                    } else if (query.getObject().equals("Dashboard")) {
                        o = dashboard;
                    } else if (query.getObject().equals("Filter")) {
                        o = filter;
                    }
                } else {
                    Exception e = new Exception("I'm sorry, Dave. I'm afraid I can't do that.\nYou are not signed in.");
                    oos.writeObject(e);
                    oos.flush();
                    continue;
                }
                Object result = executeMethod(o, function, args);
                if ((function.equals("verify") && (Boolean) result)) {
                    String[] argsString = (String[]) args;
                    loggedIn = true;
                    user = new User(argsString[0], argsString[1], Role.valueOf(db.get("email", argsString[0]).get("role")), mm, db);
                    dashboard = new Dashboard(argsString[0], mm.getHistoryLocation(db.get("email", argsString[0]).get("id")), db);
                    filter = new Filter(argsString[0], db);
                    db.modify(argsString[0], "lastOnline", Instant.now().toString());
                } else if (function.equals("logout")) {
                    db.modify(user.getEmail(), "lastOnline", Instant.now().toString());
                    loggedIn = false;
                }
                oos.writeObject(result);
                oos.flush();
            }
        } catch (Exception e) {
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            ServerSocket ss = new ServerSocket(Constants.port);
            Database db = new Database();
            MessageManager mm = new MessageManager(db);
            while (true) {
                Socket s = ss.accept();
                Server server = new Server(s, mm, db);
                new Thread(server).start();
            }
        } catch (IOException e) {
            //TODO: Change this
            e.printStackTrace();
        }
    }

    private <T> Method generateClasses(Object obj, String function, Object[] args) {
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(function) && method.getParameterTypes().length == args.length) {
                return method;
            }
        }
        return null;
    }

    private Object executeMethod(Object o, String function, Object[] args) {
        Method method = generateClasses(o, function, args);
        if (method == null) {
            return null;
        }
        Object result = null;
        if (method != null) {
            Class[] methodParameterTypes = method.getParameterTypes();
            Object[] params = null;
            if (args != null && args.length == methodParameterTypes.length) {
                params = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    try {
                        //TODO: handle for boolean casting
                        params[i] = methodParameterTypes[i].cast(args[i]);
                    } catch (ClassCastException e) {
                        //Add all the things that can't be cast as parameters here with if statements or something
                        if (function.equals("add"))
                            params[i] = Role.valueOf((String) args[i]);
                        else if (function.equals("block") || function.equals("unblock"))
                            params[i] = (Boolean) args[i];
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
            try {
                result = method.invoke(o, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                result = e.getCause();
            }
        }
        return result;
    }

}
