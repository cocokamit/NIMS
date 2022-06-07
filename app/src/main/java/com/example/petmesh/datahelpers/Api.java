package com.example.petmesh.datahelpers;

public class Api {
    private static final String ROOT_URL = "https://meshnetworksinc.com/PetMeshphp/v1/Api.php?apicall=";

    public static final String URL_CREATE_ACCOUNT = ROOT_URL + "createaccount";
    public static final String URL_INSERT_PET = ROOT_URL + "insertpets";
    public static final String URL_UPDATE_PET = ROOT_URL + "updatepets";
    public static final String URL_READ_PET_BY_OWNER = ROOT_URL + "getpetsbyowner";
    public static final String URL_READ_PET = ROOT_URL + "getpets";
    public static final String URL_READ_ACOUNT = ROOT_URL + "getusers";
    public static final String URL_READ_DOCTOR_ACCOUNT = ROOT_URL + "getdoctorusers";
    public static final String URL_UPDATE_ACCOUNT = ROOT_URL + "updateaccount";
    public static final String URL_DELETE_PET = ROOT_URL + "deletepets";
    public static final String URL_DELETE_ACCOUNT = ROOT_URL + "deleteaccount&id=";
    public static final String URL_READ_ACCOUNT_BY_ID = ROOT_URL + "readaccount&id=";
    public static final String URL_READ_ACCOUNT_BY_LOGIN = ROOT_URL + "getuserbylogin";
    public static final String URL_READ_PET_STATS = ROOT_URL + "getAllPetStatsDetailsInMonths";
    public static final String URL_READ_INVENTORY = ROOT_URL + "getinventory";
    public static final String URL_INSERT_ITEM = ROOT_URL + "insertinventory";
    public static final String URL_DELETE_ITEM = ROOT_URL + "deleteinventory";
    public static final String URL_UPDATE_ITEM = ROOT_URL + "updateInventory";
    public static final String URL_SEARCH_PETS = ROOT_URL + "getpetsbysearches";
    public static final String URL_READ_MICROCHIP = ROOT_URL + "getmicrochip";
    public static final String URL_INSERT_MICROCHIP = ROOT_URL + "insertmicrochip";
    public static final String URL_DELETE_MICROCHIP = ROOT_URL + "deletemicrochip";
    public static final String URL_READ_PET_VACCINATION = ROOT_URL + "getpetvaccination";



}
