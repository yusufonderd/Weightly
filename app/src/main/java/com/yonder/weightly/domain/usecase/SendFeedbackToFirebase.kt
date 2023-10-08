package com.yonder.weightly.domain.usecase

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class SendFeedbackToFirebase @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore
) {

    operator fun invoke(feedbackMessage : String){
        val feedback = hashMapOf(KEY_FEEDBACK_MESSAGE to feedbackMessage)
        val feedbacks = firebaseFireStore.collection(KEY_FEEDBACK_COLLECTION)
        feedbacks.add(feedback)
    }

    companion object{
        const val KEY_FEEDBACK_COLLECTION = "feedbacks"
        const val KEY_FEEDBACK_MESSAGE = "message"
    }

}
