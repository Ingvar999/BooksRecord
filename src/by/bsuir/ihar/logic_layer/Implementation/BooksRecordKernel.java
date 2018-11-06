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
    private final int booksPerPage = 3;

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
        view.attachHandler("invitation", BooksRecordKernel.class.getDeclaredMethod("invitationHandler", String[].class));
        view.attachHandler("signup", BooksRecordKernel.class.getDeclaredMethod("signupHandler", String[].class));
        view.attachHandler("login", BooksRecordKernel.class.getDeclaredMethod("loginHandler", String[].class));
        view.attachHandler("logout", BooksRecordKernel.class.getDeclaredMethod("logoutHandler", String[].class));
        view.attachHandler("add", BooksRecordKernel.class.getDeclaredMethod("addHandler", String[].class));
        view.attachHandler("list", BooksRecordKernel.class.getDeclaredMethod("listHandler", String[].class));
        view.attachHandler("find", BooksRecordKernel.class.getDeclaredMethod("findHandler", String[].class));
        view.attachHandler("offer", BooksRecordKernel.class.getDeclaredMethod("offerHandler", String[].class));
        view.start();
    }

    public String invitationHandler(String[] parts){
        StringBuilder result = new StringBuilder();
        if (currentUser != null) {
            if (isAdmin)
                result.append("admin ");
            result.append(currentUser.email);
        }
        result.append(">");
        return result.toString();
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
            ClassNotFoundException
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
        StringBuilder result = new StringBuilder();
        if (currentUser != null){
            List<BookStruct> books = file.<BookStruct>getAll(booksFile);
            int pagesCount = books.size()/booksPerPage + (books.size()/booksPerPage == 0 ? 0 : 1);
            int page = 0;
            if (parts.length == 2){
                page = Integer.parseInt(parts[1]);
                page = (page > pagesCount ? pagesCount : page) - 1;
            }
            ListIterator<BookStruct> iter = books.listIterator(page * booksPerPage);
            result.append("page " + Integer.toString(page + 1) + "/" + Integer.toString(pagesCount) + "\n");
            int i = 0;
            while (iter.hasNext() && i++ < booksPerPage) {
                BookStruct book = iter.next();
                result.append(book.title + "  -  " + book.author + "\n");
            }
        }
        return result.toString();
    }

    public String addHandler(String[] parts)
            throws IOException,
            ClassNotFoundException,
            MessagingException
    {
        if (currentUser != null && isAdmin && parts.length == 3){
            BookStruct book = new BookStruct(parts[1], parts[2]);
            List<BookStruct> books = file.<BookStruct>getAll(booksFile);
            if (!books.contains(book)){
                file.<BookStruct>append(booksFile, book);
                List<UserStruct> users = file.<UserStruct>getAll(usersFile);
                for (UserStruct user : users){
                    Send(admin.email, adminMailPassword, user.email, "New book!", book.title + "\n" +book.author);
                }
            }
        }
        return "";
    }

    public String findHandler(String[] parts)
            throws IOException,
            ClassNotFoundException
    {
        StringBuilder result = new StringBuilder();
        if (currentUser != null && parts.length == 3){
            String title = (parts[1].equals("-") ? null : parts[1]);
            String author = (parts[2].equals("-") ? null : parts[2]);
            List<BookStruct> books = file.<BookStruct>getAll(booksFile);
            for (BookStruct book : books){
                if ((title == null || title.equals(book.title)) &&
                        (author == null || author.equals(book.author))){
                    result.append(book.title + "  -  " + book.author + "\n");
                }
            }
        }
        return  result.toString();
    }

    public String offerHandler(String[] parts)
        throws MessagingException
    {
        if (!currentUser.equals(admin)) {
            if (parts.length == 4) {
                Send(currentUser.email, parts[3], admin.email, "Offer book", parts[1] + "\n" + parts[2]);
            }
        }
        return "";
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
