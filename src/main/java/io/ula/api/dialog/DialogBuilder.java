package io.ula.api.dialog;

import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.*;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.dialog.body.DialogBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DialogBuilder {
    private Component title;
    private DialogAction afterAction = DialogAction.CLOSE;
    private List<Input> inputs = new ArrayList<>();
    private List<DialogBody> bodies = new ArrayList<>();
    private List<ActionButton> buttons = new ArrayList<>();
    private boolean cancellable = false;
    public DialogBuilder(Component title){
        this.title = title;
    }
    public DialogBuilder(){
        this.title = Component.literal("Title");
    }
    public DialogBuilder setName(Component component){
        this.title = component;
        return this;
    }
    public DialogBuilder inputs(List<Input> inputs){
        this.inputs = inputs;
        return this;
    }
    public DialogBuilder bodies(List<DialogBody> bodies){
        this.bodies = bodies;
        return this;
    }
    public DialogBuilder actions(List<Button> buttons){
        List<ActionButton> buttonList = new ArrayList<>();
        for(Button button : buttons)
            buttonList.add(button.button);
        this.buttons = buttonList;
        return this;
    }
    public DialogBuilder afterAction(DialogAction action){
        this.afterAction = action;
        return this;
    }
    public DialogBuilder cancellable(Boolean cancellable){
        this.cancellable = cancellable;
        return this;
    }
    public Dialog build(){
        CommonDialogData commonDialogData = new CommonDialogData(
                this.title,
                Optional.empty(),
                this.cancellable,
                true,
                afterAction,
                bodies,
                inputs
        );
        return new MultiActionDialog(commonDialogData,buttons, Optional.empty(),inputs.size()+bodies.size());
    }
}
