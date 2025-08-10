import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Expense implements Serializable {
    private String category;
    private double amount;
    private String note;
    private Date date;

    public Expense(String category, double amount, String note, Date date) {
        this.category = category;
        this.amount = amount;
        this.note = note;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return String.format("%-12s | %-10.2f | %-20s | %s",
                category, amount, note, sdf.format(date));
    }
}

public class Main {
    private static final String FILE_NAME = "expenses.dat";
    private static List<Expense> expenses = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public static void main(String[] args) {
        loadExpenses();

        while (true) {
            System.out.println("\n==== Personal Expense Tracker ====");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. View Category Summary");
            System.out.println("4. Search by Date");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addExpense();
                case 2 -> viewExpenses();
                case 3 -> categorySummary();
                case 4 -> searchByDate();
                case 5 -> {
                    saveExpenses();
                    System.out.println("💾 Data saved. Exiting...");
                    return;
                }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    private static void addExpense() {
        try {
            System.out.print("Enter category (Food/Travel/etc.): ");
            String category = scanner.nextLine();

            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            System.out.print("Enter note: ");
            String note = scanner.nextLine();

            System.out.print("Enter date (dd-MM-yyyy): ");
            String dateStr = scanner.nextLine();
            Date date = sdf.parse(dateStr);

            expenses.add(new Expense(category, amount, note, date));
            System.out.println("✅ Expense added successfully!");
        } catch (ParseException e) {
            System.out.println("❌ Invalid date format! Please use dd-MM-yyyy.");
        }
    }

    private static void viewExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        System.out.println("\nCategory     | Amount     | Note                 | Date");
        System.out.println("---------------------------------------------------------------");
        for (Expense e : expenses) {
            System.out.println(e);
        }
    }

    private static void categorySummary() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        Map<String, Double> categoryTotal = new HashMap<>();
        for (Expense e : expenses) {
            categoryTotal.put(e.getCategory(),
                    categoryTotal.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }
        System.out.println("\nCategory Summary:");
        for (Map.Entry<String, Double> entry : categoryTotal.entrySet()) {
            System.out.printf("%-12s : %.2f\n", entry.getKey(), entry.getValue());
        }
    }

    private static void searchByDate() {
        try {
            System.out.print("Enter date (dd-MM-yyyy): ");
            String dateStr = scanner.nextLine();
            Date searchDate = sdf.parse(dateStr);

            boolean found = false;
            System.out.println("\nCategory     | Amount     | Note                 | Date");
            System.out.println("---------------------------------------------------------------");
            for (Expense e : expenses) {
                if (sdf.format(e.getDate()).equals(sdf.format(searchDate))) {
                    System.out.println(e);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No expenses found on that date.");
            }
        } catch (ParseException e) {
            System.out.println("❌ Invalid date format! Please use dd-MM-yyyy.");
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadExpenses() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            expenses = (List<Expense>) ois.readObject();
            System.out.println("📂 Loaded " + expenses.size() + " expenses from file.");
        } catch (FileNotFoundException e) {
            System.out.println("📂 No existing data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Error loading data: " + e.getMessage());
        }
    }

    private static void saveExpenses() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(expenses);
        } catch (IOException e) {
            System.out.println("❌ Error saving data: " + e.getMessage());
        }
    }
}
