package org.sadoke.worldboard.ui.main;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.sadoke.worldboard.MainActivity;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class MainViewModel extends ViewModel implements HasDefaultViewModelProviderFactory {
    MainActivity mainActivity;

    public MainViewModel(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewModelProvider.Factory getDefaultViewModelProviderFactory() {
        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                try {
                    return modelClass.getConstructor(MainActivity.class).newInstance(mainActivity);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    Log.e("ViewModelProviderFactory", Objects.requireNonNull(e.getMessage()));
                }
                return null;
            }
        };
    }
}