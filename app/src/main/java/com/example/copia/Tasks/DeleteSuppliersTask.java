package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.copia.Adapters.SuppliersAdapter;
import com.example.copia.DatabaseOperation.DeleteFiles;
import com.example.copia.DatabaseOperation.DeleteImages;
import com.example.copia.DatabaseOperation.DeleteNotes;
import com.example.copia.DatabaseOperation.DeleteReference;
import com.example.copia.Entities.SuppliersEntity;
import com.example.copia.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class DeleteSuppliersTask extends AsyncTask<Void, Void, Boolean> {
    int pos;
    List<SuppliersEntity> suppliersEntities;
    SuppliersAdapter suppliersAdapter;
    DeleteReference deleteReference = new DeleteReference();
    DeleteImages deleteImages = new DeleteImages();
    DeleteNotes deleteNotes = new DeleteNotes();
    DeleteFiles deleteFiles = new DeleteFiles();
    String objID;
    Context context;
    AlertDialog dialog;

    public DeleteSuppliersTask(String objID, Context context) {
        this.objID = objID;
        this.context = context;
        dialog = new SpotsDialog.Builder()
                .setMessage("Deleting references")
                .setContext(context)
                .setCancelable(false)
                .build();
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        List<Boolean> results = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<List<Boolean>> cf = CompletableFuture.runAsync(()->deleteReference.suppliers_delete(objID))
                    .supplyAsync(()->deleteImages.suppliers_images_delete(objID))
                    .thenApplyAsync(data->deleteNotes.suppliers_notes_delete(objID,data))
                    .thenApplyAsync(data->deleteFiles.suppliers_files_delete(objID,data));
            try {
                results = cf.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            deleteReference.suppliers_delete(objID);
            List<Boolean> first = deleteImages.suppliers_images_delete(objID);
            List<Boolean> second = deleteNotes.suppliers_notes_delete(objID, first);
            results = deleteFiles.suppliers_files_delete(objID,second);
        }

        return results.contains(false);
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        if(aBoolean == false)
        {
            suppliersEntities.remove(pos);
            suppliersAdapter.notifyItemRemoved(pos);
            Utilities.getInstance().showAlertBox("Successful", "Record deleted", context);
        }
        else
            Utilities.getInstance().showAlertBox("Error", "Some of the references were not\ndeleted properly. Please retry the deletion process", context);
    }

    public void setPos(int pos) {this.pos = pos;}
    public void setSuppliersEntities(List<SuppliersEntity> suppliersEntities) {this.suppliersEntities = suppliersEntities;}
    public void setSuppliersAdapter(SuppliersAdapter suppliersAdapter) {this.suppliersAdapter = suppliersAdapter;}
}
