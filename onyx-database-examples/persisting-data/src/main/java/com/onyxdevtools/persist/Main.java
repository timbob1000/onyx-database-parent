package com.onyxdevtools.persist;

import com.onyx.exception.EntityException;

public class Main
{

    public static void main(String[] args) throws EntityException
    {

        SavingAnEntityExample.main(args);
        BatchSavingDataExample.main(args);
        
        DeletingAnEntityExample.main(args);
        BatchDeletingDataExample.main(args);

    }
}
