package se.artcomputer.photo;

public class Renamer {

    public static void main(String[] args) {
        if (args.length == 0) {
            new RenamerGui().show();
        }
    }
}
