package by.bsuir.ihar.logic_layer.Implementation;

import by.bsuir.ihar.data_layer.Interface.IDataTransfer;
import by.bsuir.ihar.view_layer.Interface.IConsoleInterface;

import com.sun.mail.smtp.SMTPTransport;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ListIterator;

class UserStruct implements Serializable {
    public String email;
    public byte[] passwordhash;

    public UserStruct(String email, byte[] passwordhash){
        this.email = email;
        this.passwordhash = passwordhash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!UserStruct.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final UserStruct other = (UserStruct) obj;
        if ((this.email == null) ? (other.email != null) : !this.email.equals(other.email)) {
            return false;
        }
        if (this.passwordhash.equals(other.passwordhash)) {
            return false;
        }
        return true;
    }
}

class BookStruct implements Serializable{
    public String title;
    public String author;

    public  BookStruct(String title, String author){
        this.title = title;
        this.author = author;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!BookStruct.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final BookStruct other = (BookStruct) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.author == null) ? (other.author != null) : !this.author.equals(other.author)) {
            return false;
        }
        return true;
    }
}

public class BooksRecordKernel {
    private final String usersFile = "Users.txt";
    private final String booksFile = "Books.txt";
    private final String adminMailPassword = "potroschitel9976";

    private IConsoleInterface view;
    private IDataTransfer file;
    private MessageDigest md;
    private UserStruct admin;
    private UserStruct currentUser;
    private boolean isAdmin;

    public void start(IConsoleInterface view, IDataTransfer dataTransfer)
            throws NoSuchMethodException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException,
            NoSuchAlgorithmException
    {
        this.view = view;
        this.file = dataTransfer;
        md = MessageDigest.getInstance("MD5");
        admin = new UserStruct("shymanski999@gmail.com", md.digest("gg".getBytes()));
        view.attachHandler("invitation", BooksRecordKernel.class.getDeclaredMethod("invitationHandler", new Class[]{String[].class}));
        view.attachHandler("signup", BooksRecordKernel.class.getDeclaredMethod("signupHandler", new Class[]{String[].class}));
        view.attachHandler("login", BooksRecordKernel.class.getDeclaredMethod("loginHandler", new Class[]{String[].class}));
        view.attachHandler("logout", BooksRecordKernel.class.getDeclaredMethod("logoutHandler", new Class[]{String[].class}));
        view.attachHandler("add", BooksRecordKernel.class.getDeclaredMethod("addHandler", new Class[]{String[].class}));
        view.attachHandler("list", BooksRecordKernel.class.getDeclaredMethod("listHandler", new Class[]{String[].class}));
        view.attachHandler("find", BooksRecordKernel.class.getDeclaredMethod("findHandler", new Class[]{String[].class}));
        view.start();
    }

    public String invitationHandler(String[] parts){
        String result = "";
        if (currentUser != null) {
            if (isAdmin)
                result += "admin ";
            result += currentUser.email;
        }
        result += ">";
        return result;
    }

    public String logoutHandler(String[] parts){
        currentUser = null;
        return "";
    }

    public String signupHandler(String[] parts)
            throws IOException,
            ClassNotFoundException
    {
        if (parts.length == 3){
            UserStruct user = new UserStruct(parts[1], md.digest(parts[2].getBytes()));
            if (!user.equals(admin) && !file.<UserStruct>getAll(usersFile).contains(user)) {
                file.<UserStruct>append(usersFile, user);
                currentUser = user;
                isAdmin = false;
            }
        }
        return "";
    }

    public String loginHandler(String[] parts)
            throws IOException,
            ClassNotFoundException,
            MessagingException
    {
        if (parts.length == 3){
            UserStruct user = new UserStruct(parts[1], md.digest(parts[2].getBytes()));
            if (user.equals(admin)){
                currentUser = admin;
                isAdmin = true;
                return "";
            }
            List<UserStruct> users = file.<UserStruct>getAll(usersFile);
            if (users.contains(user)){
                currentUser = user;
                isAdmin = false;
                return "";
            }
        }
        return "";
    }

    public String listHandler(String[] parts)
            throws IOException,
            ClassNotFoundException
    {
        String result = "";
        if (currentUser != null){
            List<BookStruct> books = file.<BookStruct>getAll(booksFile);
            ListIterator<BookStruct> iter = books.listIterator();
            while (iter.hasNext()){
                BookStruct book = iter.next();
                result += book.title + "  -  " + book.author + "\n";
            }
        }
        return result;
    }

    public String addHandler(String[] parts)
            throws IOException,
            ClassNotFoundException
    {
        if (currentUser != null && isAdmin && parts.length == 3){
            BookStruct book = new BookStruct(parts[1], parts[2]);
            List<BookStruct> books = file.<BookStruct>getAll(booksFile);
            if (!books.contains(book)){
                file.<BookStruct>append(booksFile, book);
            }
        }
        return "";
    }

    public String findHandler(String[] parts)
            throws IOException,
            ClassNotFoundException
    {
        String result = "";
        if (currentUser != null && parts.length == 3){
            String title = (parts[1].equals("-") ? null : parts[1]);
            String author = (parts[2].equals("-") ? null : parts[2]);
            ListIterator<BookStruct> books = file.<BookStruct>getAll(booksFile).listIterator();
            while (books.hasNext()){
                BookStruct book = books.next();
                if ((title == null || title.equals(book.title)) &&
                        (author == null || author.equals(book.author))){
                    result += book.title + "  -  " + book.author + "\n";
                }
            }
        }
        return  result;
    }

    private static void Send(final String username, final String password, String recipientEmail, String title, String message)
            throws MessagingException
    {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        msg.setSubject(title);
        msg.setText(message, "utf-8");
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
}
