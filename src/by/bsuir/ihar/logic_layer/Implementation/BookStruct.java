package by.bsuir.ihar.logic_layer.Implementation;

import java.io.Serializable;

class BookStruct implements Serializable {
    String title;
    String author;

    BookStruct(String title, String author){
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
