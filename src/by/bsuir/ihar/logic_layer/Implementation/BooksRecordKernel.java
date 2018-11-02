package by.bsuir.ihar.logic_layer.Implementation;

import by.bsuir.ihar.data_layer.Interface.IDataTransfer;
import by.bsuir.ihar.view_layer.Interface.IConsoleInterface;

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
        admin = new UserStruct("gg", md.digest("gg".getBytes()));
        view.attachHandler("invitation", BooksRecordKernel.class.getDeclaredMethod("invitationHandler", new Class[]{String[].class}));
        view.attachHandler("signup", BooksRecordKernel.class.getDeclaredMethod("signupHandler", new Class[]{String[].class}));
        view.attachHandler("login", BooksRecordKernel.class.getDeclaredMethod("loginHandler", new Class[]{String[].class}));
        view.attachHandler("add", BooksRecordKernel.class.getDeclaredMethod("addHandler", new Class[]{String[].class}));
        view.attachHandler("list", BooksRecordKernel.class.getDeclaredMethod("listHandler", new Class[]{String[].class}));
        view.start();
    }

    public void invitationHandler(String[] parts){
        if (currentUser != null) {
            if (isAdmin)
                System.out.print("admin ");
            System.out.print(currentUser.email);
        }
        System.out.print('>');
    }

    public void signupHandler(String[] parts)
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
    }

    public void loginHandler(String[] parts)
            throws IOException,
            ClassNotFoundException
    {
        if (parts.length == 3){
            UserStruct user = new UserStruct(parts[1], md.digest(parts[2].getBytes()));
            if (user.equals(admin)){
                currentUser = admin;
                isAdmin = true;
                return;
            }
            List<UserStruct> users = file.<UserStruct>getAll(usersFile);
            if (users.contains(user)){
                currentUser = user;
                isAdmin = false;
                return;
            }
        }
    }

    public void listHandler(String[] parts)
            throws IOException,
            ClassNotFoundException
    {
        if (currentUser != null){
            List<BookStruct> books = file.<BookStruct>getAll(booksFile);
            ListIterator<BookStruct> iter = books.listIterator();
            while (iter.hasNext()){
                BookStruct book = iter.next();
                System.out.println(book.title + "  -  " + book.author);
            }
        }
    }

    public void addHandler(String[] parts)
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
    }
}
