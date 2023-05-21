package advisor.view;

import advisor.model.Printable;

import java.util.List;

public class View {

    public void printObjects(List<? extends Printable> objects) {
        for (Printable object : objects) {
            object.print();
        }
    }

    public void printMessages(List<String> messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    public void printUnauthorized() {
        System.out.println("Please, provide access for application.");
    }
}
