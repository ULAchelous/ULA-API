package io.ula.api.dialog;

import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.action.Action;
import net.minecraft.server.dialog.action.CustomAll;

import java.util.Optional;

public class Button{
    public ActionButton button;
    public Button(CommonButtonData commonButtonData, Action action){
        button = new ActionButton(commonButtonData,Optional.of(action));
    }
    public Button(CommonButtonData commonButtonData, CustomAll customAll, DialogHelper.CustomEvent customEvent){
        button = new ActionButton(commonButtonData,Optional.of(customAll));
        DialogHelper.addCustomClick(customAll.id(),customEvent);
    }
}
