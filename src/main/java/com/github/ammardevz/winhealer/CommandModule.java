package com.github.ammardevz.winhealer;

public class CommandModule {

    private String name;
    private String cmd;

    public CommandModule(String name, String cmd) {
        this.name = name;
        this.cmd = cmd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "CommandModule{" +
                "name='" + name + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}
