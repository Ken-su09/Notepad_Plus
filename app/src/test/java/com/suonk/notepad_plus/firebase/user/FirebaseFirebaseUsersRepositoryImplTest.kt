package com.suonk.notepad_plus.firebase.user

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.suonk.notepad_plus.model.database.data.entities.CustomFirebaseUser
import io.mockk.CapturingSlot
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class FirebaseUsersRepositoryImplTest {


    private val firebaseFirestore: FirebaseFirestore = mockk()
    private val collectionReference: CollectionReference = mockk()
    private val query: Query = mockk()
    private val taskQuerySnapshot: Task<QuerySnapshot> = mockk()
    private val taskQuerySnapshotMock: Task<QuerySnapshot> = mockk()
    private val documentReference: DocumentReference = mockk()
    private val taskVoid: Task<Void> = mockk()

    private val firebaseUsersRepositoryImpl = FirebaseUsersRepositoryImpl(firebaseFirestore)

    @Before
    fun setup() {

    }

    @Test
    fun `test add new user to firestore if result is true`() {
        every { firebaseFirestore.collection(ALL_USERS) } returns collectionReference
        every { collectionReference.whereEqualTo(ID, DEFAULT_ID) } returns query
        every { query.get() } returns taskQuerySnapshot

        val querySnapshotSlot: CapturingSlot<OnCompleteListener<QuerySnapshot>> = slot()
        every { taskQuerySnapshot.addOnCompleteListener(capture(querySnapshotSlot)) } answers {
            querySnapshotSlot.captured.onComplete(taskQuerySnapshotMock)
            taskQuerySnapshotMock
        }
        every { taskQuerySnapshotMock.result.isEmpty } returns true
        every { collectionReference.document(DEFAULT_ID) } returns documentReference
        every { documentReference.set(defaultCustomFirebaseUser()) } returns taskVoid

        firebaseUsersRepositoryImpl.addNewUserToFirestore(DEFAULT_ID, defaultCustomFirebaseUser())

        verify {
            firebaseFirestore.collection(ALL_USERS)
            collectionReference.whereEqualTo(ID, DEFAULT_ID)
            query.get()
            taskQuerySnapshot.addOnCompleteListener(capture(querySnapshotSlot))
            taskQuerySnapshotMock.result.isEmpty
            collectionReference.document(DEFAULT_ID)
            documentReference.set(defaultCustomFirebaseUser())
        }

        confirmVerified(firebaseFirestore)
    }

    @Test
    fun `test add new user to firestore if result is false`() {
        every { firebaseFirestore.collection(ALL_USERS) } returns collectionReference
        every { collectionReference.whereEqualTo(ID, DEFAULT_ID) } returns query
        every { query.get() } returns taskQuerySnapshot

        val querySnapshotSlot: CapturingSlot<OnCompleteListener<QuerySnapshot>> = slot()
        every { taskQuerySnapshot.addOnCompleteListener(capture(querySnapshotSlot)) } answers {
            querySnapshotSlot.captured.onComplete(taskQuerySnapshotMock)
            taskQuerySnapshotMock
        }
        every { taskQuerySnapshotMock.result.isEmpty } returns false

        firebaseUsersRepositoryImpl.addNewUserToFirestore(DEFAULT_ID, defaultCustomFirebaseUser())

        verify {
            firebaseFirestore.collection(ALL_USERS)
            collectionReference.whereEqualTo(ID, DEFAULT_ID)
            query.get()
            taskQuerySnapshot.addOnCompleteListener(capture(querySnapshotSlot))
            taskQuerySnapshotMock.result.isEmpty
        }

        confirmVerified(firebaseFirestore)
    }

    private fun defaultCustomFirebaseUser(): CustomFirebaseUser {
        return CustomFirebaseUser(
            id = DEFAULT_ID,
            name = DEFAULT_NAME,
            mail = DEFAULT_MAIL,
            photo = DEFAULT_PHOTO_URL
        )
    }

    companion object {
        private const val ALL_USERS = "ALL_USERS"
        private const val ID = "ID"

        private const val DEFAULT_ID = "DEFAULT_ID"
        private const val DEFAULT_NAME = "DEFAULT_NAME"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PHOTO_URL = "DEFAULT_PHOTO_URL"
    }
}