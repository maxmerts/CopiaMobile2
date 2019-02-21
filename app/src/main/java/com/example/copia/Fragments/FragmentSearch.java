package com.example.copia.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.copia.Adapters.ClientAdapter;
import com.example.copia.Adapters.ConsultantsAdapter;
import com.example.copia.Adapters.ContractorsAdapter;
import com.example.copia.Adapters.SpecificationsAdapter;
import com.example.copia.Adapters.SuppliersAdapter;
import com.example.copia.Entities.ComboboxEntity;
import com.example.copia.DatabaseOperation.DeleteFiles;
import com.example.copia.DatabaseOperation.DeleteImages;
import com.example.copia.DatabaseOperation.DeleteNotes;
import com.example.copia.DatabaseOperation.DeleteReference;
import com.example.copia.Entities.ClientEntity;
import com.example.copia.Entities.ConsultantsEntity;
import com.example.copia.Entities.ContractorsEntity;
import com.example.copia.Entities.SpecificationsEntity;
import com.example.copia.Entities.SuppliersEntity;
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.Tasks.ClientSearchTask;
import com.example.copia.Tasks.ConsultantsSearchTask;
import com.example.copia.Tasks.ContractorsSearchTask;
import com.example.copia.Tasks.DeleteClientTask;
import com.example.copia.Tasks.DeleteConsultantsTask;
import com.example.copia.Tasks.DeleteContractorsTask;
import com.example.copia.Tasks.DeleteSpecificationsTask;
import com.example.copia.Tasks.DeleteSuppliersTask;
import com.example.copia.Tasks.SpecificationsSearchTask;
import com.example.copia.Tasks.SuppliersSearchTask;
import com.example.copia.Utilities;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;
import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;


public class FragmentSearch extends Fragment
{
    private ClientAdapter clientAdapter;
    private SuppliersAdapter suppliersAdapter;
    private ContractorsAdapter contractorsAdapter;
    private ConsultantsAdapter consultantsAdapter;
    private SpecificationsAdapter specificationsAdapter;
    String searchin = null;
    private List<ClientEntity> clientEntities;
    private List<SuppliersEntity> suppliersEntities;
    private List<ContractorsEntity> contractorsEntities;
    private List<ConsultantsEntity> consultantsEntities;
    private List<SpecificationsEntity> specificationsEntities;
    EditText search_edittext_search;
    Spinner search_spinner_searchin;
    RecyclerView search_recyclerview;
    boolean option;
    int pos = -1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ArrayList<String> searchCategories = new ArrayList<>();
        List<ComboboxEntity> comboboxEntities = ComboboxEntity.findWithQuery(ComboboxEntity.class, "Select distinct category from combobox_entity");
        for(ComboboxEntity entity : comboboxEntities)
            searchCategories.add(entity.getCategory());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, searchCategories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search_recyclerview = (RecyclerView)view.findViewById(R.id.search_recyclerview);
        search_recyclerview.setHasFixedSize(true);
        search_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        option = false;
        search_recyclerview.setOnTouchListener(listener());
        search_spinner_searchin = (Spinner)view.findViewById(R.id.search_spinner_searchin);
        search_spinner_searchin.setAdapter(dataAdapter);
        search_edittext_search = (EditText)view.findViewById(R.id.search_edittext_search);
        search_edittext_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String keyword = search_edittext_search.getText().toString().trim().toUpperCase();
                    searchin = search_spinner_searchin.getSelectedItem().toString();
                    if(searchin.equalsIgnoreCase("Client"))
                    {
                        ClientSearchTask clientSearchTask = new ClientSearchTask(getContext(), keyword, search_recyclerview);
                        clientSearchTask.execute((Void)null);
                        clientAdapter = new ClientAdapter(getContext(), clientSearchTask.getClientEntities());
                        clientEntities = clientSearchTask.getClientEntities();
                    }
                    else if(searchin.equalsIgnoreCase("Suppliers"))
                    {
                        SuppliersSearchTask suppliersSearchTask = new SuppliersSearchTask(search_recyclerview, getContext(), keyword);
                        suppliersSearchTask.execute((Void)null);
                        suppliersAdapter = new SuppliersAdapter(getContext(), suppliersSearchTask.getSuppliersEntities());
                        suppliersEntities = suppliersSearchTask.getSuppliersEntities();
                    }
                    else if(searchin.equalsIgnoreCase("Contractors"))
                    {
                        ContractorsSearchTask contractorsSearchTask = new ContractorsSearchTask(search_recyclerview, getContext(), keyword);
                        contractorsSearchTask.execute((Void)null);
                        contractorsAdapter = new ContractorsAdapter(getContext(), contractorsSearchTask.getContractorsEntities());
                        contractorsEntities = contractorsSearchTask.getContractorsEntities();
                    }
                    else if(searchin.equalsIgnoreCase("Consultants"))
                    {
                        ConsultantsSearchTask consultantsSearchTask = new ConsultantsSearchTask(search_recyclerview, getContext(), keyword);
                        consultantsSearchTask.execute((Void)null);
                        consultantsAdapter = new ConsultantsAdapter(getContext(), consultantsSearchTask.getConsultantsEntities());
                        consultantsEntities = consultantsSearchTask.getConsultantsEntities();
                    }
                    else if(searchin.equalsIgnoreCase("Specifications"))
                    {
                        SpecificationsSearchTask specificationsSearchTask = new SpecificationsSearchTask(search_recyclerview, getContext(), keyword);
                        specificationsSearchTask.execute((Void)null);
                        specificationsAdapter = new SpecificationsAdapter(getContext(), specificationsSearchTask.getSpecificationsEntities());
                        specificationsEntities = specificationsSearchTask.getSpecificationsEntities();
                    }

                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(clientAdapter != null)
            search_recyclerview.setAdapter(clientAdapter);
        else if(suppliersAdapter != null)
            search_recyclerview.setAdapter(suppliersAdapter);
        else if(contractorsAdapter != null)
            search_recyclerview.setAdapter(contractorsAdapter);
        else if(consultantsAdapter != null)
            search_recyclerview.setAdapter(consultantsAdapter);
        else if(specificationsAdapter != null)
            search_recyclerview.setAdapter(specificationsAdapter);
    }

    private SwipeDismissRecyclerViewTouchListener listener()
    {
        SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(
                search_recyclerview,
                new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        pos = position;
                        return true;
                    }

                    @Override
                    public void onDismiss(View view) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        if(search_recyclerview.getAdapter().getClass().getSimpleName().equalsIgnoreCase("ClientAdapter"))
                                        {
                                            DeleteClientTask deleteClientTask = new DeleteClientTask(clientEntities.get(pos).getObjectId(), getContext());
                                            deleteClientTask.setPos(pos);
                                            deleteClientTask.setClientAdapter(clientAdapter);
                                            deleteClientTask.setClientEntities(clientEntities);
                                            deleteClientTask.execute((Void)null);
                                        }
                                        else if(search_recyclerview.getAdapter().getClass().getSimpleName().equalsIgnoreCase("SuppliersAdapter"))
                                        {
                                            DeleteSuppliersTask deleteSuppliersTask = new DeleteSuppliersTask(suppliersEntities.get(pos).getObjectId(), getContext());
                                            deleteSuppliersTask.setPos(pos);
                                            deleteSuppliersTask.setSuppliersAdapter(suppliersAdapter);
                                            deleteSuppliersTask.setSuppliersEntities(suppliersEntities);
                                            deleteSuppliersTask.execute((Void)null);
                                        }
                                        else if(search_recyclerview.getAdapter().getClass().getSimpleName().equalsIgnoreCase("ContractorsAdapter"))
                                        {
                                            DeleteContractorsTask deleteContractorsTask = new DeleteContractorsTask(contractorsEntities.get(pos).getObjectId(), getContext());
                                            deleteContractorsTask.setPos(pos);
                                            deleteContractorsTask.setContractorsAdapter(contractorsAdapter);
                                            deleteContractorsTask.setContractorsEntities(contractorsEntities);
                                            deleteContractorsTask.execute((Void)null);
                                        }
                                        else if(search_recyclerview.getAdapter().getClass().getSimpleName().equalsIgnoreCase("ConsultantsAdapter"))
                                        {
                                            DeleteConsultantsTask deleteConsultantsTask = new DeleteConsultantsTask(consultantsEntities.get(pos).getObjectId(), getContext());
                                            deleteConsultantsTask.setPos(pos);
                                            deleteConsultantsTask.setConsultantsAdapter(consultantsAdapter);
                                            deleteConsultantsTask.setConsultantsEntities(consultantsEntities);
                                            deleteConsultantsTask.execute((Void)null);
                                        }
                                        else if(search_recyclerview.getAdapter().getClass().getSimpleName().equalsIgnoreCase("SpecificationsAdapter"))
                                        {
                                            DeleteSpecificationsTask deleteSpecificationsTask = new DeleteSpecificationsTask(specificationsEntities.get(pos).getObjectid(), getContext());
                                            deleteSpecificationsTask.setPos(pos);
                                            deleteSpecificationsTask.setSpecificationsAdapter(specificationsAdapter);
                                            deleteSpecificationsTask.setSpecificationsEntities(specificationsEntities);
                                            deleteSpecificationsTask.execute((Void)null);
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("This record will be deleted").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                })
                .setIsVertical(false)
                .setItemTouchCallback(
                        new SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack() {
                            @Override
                            public void onTouch(int index) {
                                // Do what you want when item be touched
                            }
                        })
                .setItemClickCallback(new SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack() {
                    @Override
                    public void onClick(int i) {
                        String[] choices = new String[]{"Notes", "Image Files", "PDF Files"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Choose what to show: ");
                        builder.setCancelable(true);
                        builder.setItems(choices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                String adapterClass = search_recyclerview.getAdapter().getClass().getSimpleName();
                                String objectid = null;
                                if(adapterClass.equalsIgnoreCase("ClientAdapter"))
                                    objectid = clientEntities.get(pos).getObjectId();
                                else if(adapterClass.equalsIgnoreCase("SuppliersAdapter"))
                                    objectid = suppliersEntities.get(pos).getObjectId();
                                else if(adapterClass.equalsIgnoreCase("ContractorsAdapter"))
                                    objectid = contractorsEntities.get(pos).getObjectId();
                                else if(adapterClass.equalsIgnoreCase("ConsultantsAdapter"))
                                    objectid = consultantsEntities.get(pos).getObjectId();
                                else if(adapterClass.equalsIgnoreCase("SpecificationsAdapter"))
                                    objectid = specificationsEntities.get(pos).getObjectid();
                                if(which == 0) {
                                    ((MainActivity)getActivity()).setObjectId(objectid);
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentNotes()).addToBackStack(null).commit();
                                }
                                else if(which == 1) {
                                    ((MainActivity)getActivity()).setObjectId(objectid);
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentImages()).addToBackStack(null).commit();
                                }
                                else if(which == 2) {
                                    ((MainActivity)getActivity()).setObjectId(objectid);
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentPdf()).addToBackStack(null).commit();
                                }
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                })
                .create();
        return listener;
    }
}
