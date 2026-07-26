package io.ula.api.motd;

public interface CustomMotdHolder {

    public static enum Type{
        DIRECT_REPLACE,
        FORMARTTED_STRING,
    }

    public void setMotd(String motd, Type type);

    public void unsetMotd(String motd, Type type);
}
