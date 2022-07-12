package com.magistor8.weather.lesson9

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.magistor8.weather.databinding.FragmentContactsBinding
import com.magistor8.weather.permissions.CALL
import com.magistor8.weather.permissions.CONTACTS
import com.magistor8.weather.permissions.PermissionListener
import com.magistor8.weather.permissions.Permissions

class ContactsListFragment: Fragment() {

    private val numberIsNotSpecified = "default"
    private lateinit var permissions: Permissions


    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {ContactsFragmentAdapter()}

    private val listener by lazy {
        object : OnItemViewClickListener {
            override fun onItemViewClick(contact: Contact) {
                val callPermissionListener = object : PermissionListener {
                    override fun callBack() {
                        phoneCall(contact.phones[0])
                    }
                }
                //Спрашиваем разрешение
                permissions.checkPermission(CALL, callPermissionListener)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        adapter.setOnClickListener(listener)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.contactsFragmentRecyclerview.adapter = adapter
        permissions = Permissions(requireActivity(), this)
        //Спрашиваем разрешение
        val contactPermissionListener = object : PermissionListener {
            override fun callBack() {
                getContacts()
            }
        }
        permissions.checkPermission(CONTACTS, contactPermissionListener)
    }

    private fun getContacts() {
        context?.let {
            // Получаем ContentResolver у контекста
            val contentResolver: ContentResolver = it.contentResolver
            // Отправляем запрос на получение контактов и получаем ответ в виде Cursor
            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )

            cursorWithContacts?.let { cursor ->
                for (i in 0..cursor.count) {
                    // Переходим на позицию в Cursor
                    if (cursor.moveToPosition(i)) {
                        lateinit var phones: List<String>
                        val id = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID))
                        val name = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        phones = if (Integer.parseInt(cursor.getString(cursor
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            getContactPhones(it, id)
                        } else {
                            listOf()
                        }
                        adapter.addData(Contact(name, phones))
                    }
                }
            }
            cursorWithContacts?.close()

            //Убираем лэйаут загрузки
            binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
        }
    }

    private fun getContactPhones(it: Context, id: String): List<String> {
        val phones: MutableList<String> = mutableListOf()
        val cursorWithPhones = it.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
            null,
            null
        )
        cursorWithPhones?.let { cursor : Cursor ->
            for (i in 0..cursor.count) {
                if (cursor.moveToPosition(i)) {
                    val phone = cursorWithPhones.getString(cursorWithPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    phones.add(phone)
                }
            }
        }
        cursorWithPhones?.close()
        return phones.toList()
    }

    private fun phoneCall(number: String) {
        if (number != numberIsNotSpecified) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$number")
            startActivity(callIntent)
        } else {
            Toast.makeText(context, "Number is not specified", Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ContactsListFragment()
    }

    interface OnItemViewClickListener {
        fun onItemViewClick(contact: Contact)
    }

}