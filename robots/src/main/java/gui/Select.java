package gui;

public enum Select {
    YES("Да"),
    NO("Нет");

    private String res;
    Select(String response) {
        res = response;
    }

    @Override
    public String toString() {
        return res;
    }
}
