package petstore;

public class Pet {
    public int id;
    public class Category{ //disse o que é, subclasse
        public int id;
        public String name;
    }
    public Category category; //incluir 
    public String name;
    public String[] photoUrls;
    public class Tag{
        public int id;
        public String name;
    }
    public Tag tags[];
    public String status;
}
