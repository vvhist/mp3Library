import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Application {

    // Между методами верхнего уровня стоит оставлять одну строку, чтобы их было удонее читать
    private static CommandLine createCommandLineOptions(String[] args) {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        Option search  = Option.builder().longOpt("search").valueSeparator().hasArgs().build();
        Option rebuild = Option.builder().longOpt("rebuild").hasArg(false).build();
        options.addOption(search);
        options.addOption(rebuild);
        try {
            // return null; служит индикатором ошибки,
            // а следовательно больше информации метод выше получит если ему ошибка прокинется полностью
            return parser.parse(options, args);
        } catch (ParseException e) {
            return null;
        }
    }

    // Пустая строка между методами, метод должен быть private если ты не собираешься его публиковать
    // Согласно соглашению о наименовании boolean методы следует называть isFirstArgumentFolder (вопрос)
    static boolean firstArgumentIsFolder(String[] args) {
        return args.length != 0
            && new File(args[0]).isDirectory(); // Если условия в return разделены переносом строки, то их проще читать
    }

    // Метод слишком длинный, больше 30 строк уже сложно читать
    public static void main(String[] args) {
        CommandLine cmd = createCommandLineOptions(args);
        if (cmd == null) {
            // Зачем если можно поймать исключение и сделать то же самое в catch блоке вместо проверки на null
            System.err.println("Parse error");
            return;
        }
        if (!firstArgumentIsFolder(args)) {
            System.out.println("Please enter the path of your MusicFolder!");
            return;
        }
        File musicFolder = new File(args[0]); // Проще было сразу использовать файл и избавиться от предыдущей проверки
        // musicFolder (File) неявно преобразуется в строку (String) - так быть не должно, .toString не обязан
        // формировать правильный путь до файла, правильно: new File(musicFolder, "database.ser")
        String databasePath = musicFolder + File.separator + "database.ser";

        if (!new File(databasePath).exists() || cmd.hasOption("rebuild")) {
            ArrayList<File> data = MusicData.create(musicFolder);
            if (data == null) {
                System.out.println("No .mp3 files were found!"); // catch, report error, exit
                return;
            }
            // merge with the previous try/catch
            try {
                Database.serialize(databasePath, data);
                System.out.println("Data is saved in " + databasePath);
            } catch (IOException e) {
                System.err.println("Error: the program failed to save the data in " + databasePath);
                return;
            }
        }
        if (cmd.hasOption("search")) {      // Add your search keys in DataSearch.createTagsMap()
            Database database = Database.deserialize(databasePath);
            if (database == null) { // catch, report, exit
                System.err.println("Error: the program failed to get the data from " + databasePath);
                return;
            }
            for (String name : DataSearch.getResults(cmd.getOptionValues("search"), database.data)) {
                System.out.println(name);
            }
        }
    }
}