import controller.LibraryController;
import java.awt.GraphicsEnvironment;
import patterns.facade.LibraryManagementFacade;
import repository.IDataRepository;
import repository.InMemoryRepository;
import test.TestRunner;
import view.ConsoleLibraryView;
import view.ILibraryView;
import view.SwingLibraryView;

public class MainApp {
    public static void main(String[] args) {
        if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            TestRunner.main(args);
            return;
        }
        if (args.length > 0 && "--console".equalsIgnoreCase(args[0])) {
            startApp(new ConsoleLibraryView());
            return;
        }

        if (GraphicsEnvironment.isHeadless()) {
            startApp(new ConsoleLibraryView());
        } else {
            startApp(new SwingLibraryView());
        }
    }

    private static void startApp(ILibraryView view) {
        IDataRepository repository = new InMemoryRepository();
        LibraryManagementFacade facade = new LibraryManagementFacade(repository);
        LibraryController controller = new LibraryController(facade, view);
        controller.startApplication();
    }
}
