package app.domain;

public enum Status {
    INITIAL("initial"), UNDERAGE("underage"), ACTIVE("active"), DECEASED("deceased");

    private String _status;

    private Status(String value) {
        this._status = value;
    }

    public String toString() {
        return this._status;
    }
}
