package com.online.course.manager.net

import com.online.course.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiClient {
    @Headers("Content-Type: application/json")
    @POST("login")
    fun login(@Body login: Login): Call<Data<Response>>

    @GET("config")
    fun getAppConfig(): Call<AppConfig>

    @Headers("Content-Type: application/json")
    @POST("register/step/1")
    fun signUpMethod(@Body signUp: EmailSignUp): Call<Data<User>>

    @Headers("Content-Type: application/json")
    @POST("register/step/1")
    fun signUpMethod(@Body signUp: MobileSignUp): Call<Data<User>>

    @Headers("Content-Type: application/json")
    @POST("register/step/2")
    fun verifyAccount(@Body accountVerification: AccountVerification): Call<Data<User>>

    @Headers("Content-Type: application/json")
    @POST("register/step/3")
    fun registerUser(@Body user: User): Call<Data<Response>>

    @Headers("Content-Type: application/json")
    @POST("google/callback")
    fun registerWithGoogle(@Body thirdPartyLogin: ThirdPartyLogin): Call<Data<Response>>

    @Headers("Content-Type: application/json")
    @POST("facebook/callback")
    fun registerWithFacebook(@Body thirdPartyLogin: ThirdPartyLogin): Call<Data<Response>>

    @GET("courses")
    fun getCourses(@QueryMap map: Map<String, String>): Call<Data<List<Course>>>

    @GET("featured-courses")
    fun getFeaturedCourses(): Call<Data<List<Course>>>

    @GET("featured-courses")
    fun getFeaturedCourses(@Query("cat") categoryId: Int): Call<Data<List<Course>>>

    @GET("search")
    fun searchCoursesAndUsers(@Query("search") s: String): Call<Data<SearchObject>>

    @GET("trend-categories")
    fun getTrendingCategories(): Call<Data<Count<Category>>>

    @GET("categories")
    fun getCategories(): Call<Data<Count<Category>>>

    @GET("categories/{category_id}/webinars")
    fun getCatFiltersAndCourses(@Path("category_id") categoryId: Int): Call<Data<CatCourses>>

    @GET
    fun getCatFilteredCourses(@Url url: String): Call<Data<CatCourses>>

    @GET("blogs")
    fun getBlogs(): Call<Data<List<Blog>>>

    @GET("blogs")
    fun getBlogs(@Query("cat") categoryId: Int): Call<Data<List<Blog>>>

    @Headers("Content-Type: application/json")
    @POST("panel/comments")
    fun comment(@Body comment: Comment): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("panel/comments/{comment_id}/reply")
    fun reply(@Path("comment_id") commentId: Int, @Body comment: Comment): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("panel/comments/{comment_id}/report")
    fun reportComment(
        @Path("comment_id") commentId: Int,
        @Body comment: Comment
    ): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @PUT("panel/comments/{comment_id}")
    fun editComment(@Path("comment_id") commentId: Int, @Body comment: Comment): Call<BaseResponse>

    @GET
    fun getProviders(@Url url: String): Call<Data<Count<User>>>

    @GET("panel/comments")
    fun getComments(): Call<Data<Comments>>

    @Headers("Content-Type: application/json")
    @DELETE("panel/comments/{comment_id}")
    fun removeComment(@Path("comment_id") commentId: Int): Call<BaseResponse>

    @GET("panel/meetings")
    fun getMeetings(): Call<Data<Meetings>>

    @GET("panel/profile-setting")
    fun getProfileSettings(): Call<Data<UserProfile>>

    @Headers("Content-Type: application/json")
    @PUT("panel/profile-setting/password")
    fun changePassword(@Body changePassword: ChangePassword): Call<Data<Response>>

    @Headers("Content-Type: application/json")
    @PUT("panel/profile-setting")
    fun changeProfileSettings(@Body changeSettings: UserChangeSettings): Call<BaseResponse>

    @PUT("panel/profile-setting")
    fun changeProfileSettings(@Body financialSettings: FinancialSettings): Call<BaseResponse>

    @PUT("panel/profile-setting")
    fun changeProfileSettings(@Body changeSettings: UserChangeLocalization): Call<BaseResponse>

    @Multipart
    @POST("panel/profile-setting/images")
    fun changeProfileImage(@Part file: MultipartBody.Part): Call<BaseResponse>

    @Multipart
    @POST("panel/profile-setting/images")
    fun uploadFinanicalSettings(
        @Part identityFile: MultipartBody.Part,
        @Part scanFile: MultipartBody.Part
    ): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("instructor/meetings/{meeting_id}/finish")
    fun finishMeeting(@Path("meeting_id") meetingId: Int, @Body any: Any): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("instructor/meetings/create-link")
    fun createJoinForMeeting(@Body reserveMeeting: ReserveMeeting): Call<BaseResponse>

    @GET("panel/certificates/achievements")
    fun getAchievementCertificates(): Call<Data<List<QuizResult>>>

    @GET("panel/certificates/created")
    fun getClassCertificates(): Call<Data<Data<List<Quiz>>>>

    @GET("users/{user_id}/profile")
    fun getUserProfile(@Path("user_id") userId: Int): Call<Data<Data<UserProfile>>>

    @Headers("Content-Type: application/json")
    @POST("panel/users/{user_id}/follow")
    fun followUser(@Path("user_id") userId: Int, @Body follow: Follow): Call<BaseResponse>

    @GET("users/{user_id}/meetings")
    fun getAvailableMeetingTimes(
        @Path("user_id") userId: Int,
        @Query("date") date: String
    ): Call<Data<Count<Timing>>>

    @Headers("Content-Type: application/json")
    @POST("meetings/reserve")
    fun reserveMeeting(@Body reserveMeeting: ReserveTimeMeeting): Call<BaseResponse>

    @GET("panel/notifications")
    fun getNotifs(): Call<Data<Count<Notif>>>

    @Headers("Content-Type: application/json")
    @POST("panel/notifications/{id}/seen")
    fun setNotifStatusToSeen(@Path("id") notifId: Int): Call<BaseResponse>

    @GET("panel/support/tickets")
    fun getTickets(): Call<Data<List<Ticket>>>

    @GET("panel/support/class_support")
    fun getClassSupports(): Call<Data<List<Ticket>>>

    @GET("panel/support/my_class_support")
    fun getMyClassSupports(): Call<Data<List<Ticket>>>

    @GET("panel/support/departments")
    fun getDepartments(): Call<List<Department>>

    @Headers("Content-Type: application/json")
    @POST("panel/support")
    fun addTicket(@Body ticket: Ticket): Call<BaseResponse>

    @Multipart
    @POST("panel/support")
    fun addTicket(
        @Part attachment: MultipartBody.Part,
        @Part title: MultipartBody.Part,
        @Part message: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        @Part departmentId: MultipartBody.Part
    ): Call<Data<Response>>

    @Headers("Content-Type: application/json")
    @POST("panel/support/{id}/conversations")
    fun addTicketConverstation(
        @Path("id") id: Int,
        @Body conversation: Conversation
    ): Call<BaseResponse>

    @Multipart
    @POST("panel/support/{id}/conversations")
    fun addTicketConverstation(
        @Path("id") id: Int,
        @Part attachment: MultipartBody.Part,
        @Part message: MultipartBody.Part
    ): Call<Data<Response>>


    @GET("panel/support/{id}/close")
    fun closeTicket(@Path("id") ticketId: Int): Call<BaseResponse>

    @GET("panel/quizzes/results/my-results")
    fun getMyQuizzesResult(): Call<Data<Count<QuizResult>>>

    @GET("panel/quizzes/not_participated")
    fun getNotParticipatedQuizzes(): Call<Data<Count<Quiz>>>

    @GET("panel/quizzes/created")
    fun getQuizzesList(): Call<Data<Count<Quiz>>>

    @GET("panel/quizzes/results/my-student-result")
    fun getStudentResults(): Call<Data<Count<QuizResult>>>

    @GET("panel/quizzes/{id}/start")
    fun startQuiz(@Path("id") id: Int): Call<Data<QuizResult>>

    @POST("panel/quizzes/{quiz_id}/store-result")
    fun storeQuizResult(
        @Path("quiz_id") quizId: Int,
        @Body answer: QuizAnswer
    ): Call<Data<Data<QuizResult>>>

    @Headers("Content-Type: application/json")
    @POST("panel/quizzes/results/{result_id}/review")
    fun storeReviewResult(
        @Path("result_id") resultId: Int,
        @Body review: List<QuizAnswerItem>
    ): Call<BaseResponse>

    @GET("panel/financial/summary")
    fun getFinancialSummary(): Call<Data<Count<FinancialSummary>>>

    @GET("panel/quick-info")
    fun getQuickInfo(): Call<QuickInfo>

    @GET("panel/financial/offline-payments")
    fun getOfflinePayments(): Call<Data<List<OfflinePayment>>>

    @GET("panel/financial/payout")
    fun getPayouts(): Call<Data<PayoutRes>>

    @Headers("Content-Type: application/json")
    @POST("panel/financial/payout")
    fun requestPayout(@Body any: Any): Call<BaseResponse>

    @GET("panel/financial/sales")
    fun getSales(): Call<Data<SalesRes>>

    @GET("panel/financial/platform-bank-accounts")
    fun getBankInfos(): Call<Data<Count<SystemBankAccount>>>

    @Headers("Content-Type: application/json")
    @POST("panel/financial/offline-payments")
    fun addOfflinePayments(@Body offlinePayment: OfflinePayment): Call<BaseResponse>

    @GET("panel/favorites")
    fun getFavorites(): Call<Data<Count<Favorite>>>

    @Headers("Content-Type: application/json")
    @POST("panel/favorites/toggle2")
    fun addRemoveFromFavorite(@Body addToFav: AddToFav): Call<BaseResponse>

    @GET("panel/classes")
    fun getMyClassesPageData(): Call<MyClasses>

    @GET("panel/subscribe")
    fun getSubscriptions(): Call<Data<Subscription>>

    @GET("panel/cart/list")
    fun getCart(): Call<Data<Data<Cart?>>>

    @Headers("Content-Type: application/json")
    @DELETE("panel/cart/{id}")
    fun removeFromCart(@Path("id") cartItemId: Int): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("panel/cart/coupon/validate")
    fun validateCoupon(@Body coupon: Coupon): Call<Data<CouponValidation>>

    @Headers("Content-Type: application/json")
    @POST("panel/cart/web_checkout")
    fun checkout(@Body coupon: Coupon?): Call<Data<Response>>

    @Headers("Content-Type: application/json")
    @POST("users/{user_id}/send-message")
    fun addNewMessage(@Path("user_id") userId: Int, @Body message: Message): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("courses/{course_id}/report")
    fun reportCourse(@Path("course_id") courseId: Int, @Body comment: Comment): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("panel/reviews3")
    fun addCourseReview(@Body review: Review): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("panel/cart")
    fun addToCart(@Body addToCart: AddToCart): Call<BaseResponse>

    @GET("courses/{course_id}")
    fun getCourseDetails(@Path("course_id") courseId: Int): Call<Data<Course>>

    @Headers("Content-Type: application/json")
    @POST("panel/subscribe/apply")
    fun subscribe(@Body addToCart: AddToCart): Call<BaseResponse>

    @GET("panel/webinars/purchases")
    fun getMyPurchases(): Call<Data<Count<Course>>>

    @GET("panel/webinars/organization")
    fun getCoursesOfOrganizations(): Call<Data<Count<Course>>>

    @GET("panel/certificates/students")
    fun getCertificateStudents(): Call<Data<List<QuizResult>>>

    @Headers("Content-Type: application/json")
    @POST("panel/payments/request")
    fun requestPayment(@Body paymentRequest: PaymentRequest)

    @Headers("Content-Type: application/json")
    @POST("panel/financial/web_charge")
    fun chargeAccount(@Body paymentRequest: PaymentRequest): Call<Data<Response>>

    @Headers("Content-Type: application/json")
    @POST("panel/payments/credit")
    fun payWithCredit(@Body paymentRequest: PaymentRequest): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("panel/subscribe/web_pay")
    fun checkoutSubscription(@Body subscriptionItem: SubscriptionItem): Call<Data<Response>>

    @GET("panel/financial/accounts-type")
    fun getAccountTypes(): Call<Data<Count<String>>>

    @Headers("Content-Type: application/json")
    @POST("courses/{course_id}/toggle")
    fun changeLessonItemStatus(
        @Path("course_id") courseId: Int,
        @Body chapterItemMark: ChapterItemMark
    ): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("panel/webinars/{course_id}/free")
    fun addFreeCourse(@Path("course_id") courseId: Int): Call<BaseResponse>

    @GET("panel/quizzes/{quiz_id}/result")
    fun getQuizResult(@Path("quiz_id") quizId: Int): Call<Data<QuizResult>>

    @Headers("Content-Type: application/json")
    @POST("forget-password")
    fun sendChangePasswordLink(@Body forgetPassword: ForgetPassword): Call<BaseResponse>

    @GET("blogs/categories")
    fun getBlogCategories(): Call<Data<List<Category>>>

    @POST("logout")
    fun logout(@Body follow: Follow): Call<BaseResponse>

    @GET("courses/reports/reasons")
    fun getReportReasons(): Call<Data<Map<String, String>>>

    @GET("panel/rewards")
    fun getPoints(): Call<Data<Points>>

    @GET("panel/registration-packages")
    fun getSaasPackages(): Call<Data<SaasPackage>>

    @Headers("Content-Type: application/json")
    @POST("panel/registration-packages/pay")
    fun checkoutSaasPackage(@Body saasPackageItem: SaasPackageItem): Call<Data<Response>>

    @Headers("Content-Type: application/json")
    @POST("panel/rewards/webinar/{course_id}/apply")
    fun purchaseWithPoints(
        @Path("course_id") courseId: Int,
        @Body follow: Follow
    ): Call<BaseResponse>

    @Headers("Content-Type: application/json")
    @POST("panel/bundles/{bundle_id}/buyWithPoint")
    fun bundlePurchaseWithPoints(
        @Path("bundle_id") bundleId: Int,
        @Body follow: Follow
    ): Call<BaseResponse>

    @GET("regions/countries")
    fun getCountries(): Call<Data<List<Region>>>

    @GET("regions/provinces/{country_id}")
    fun getProvinces(@Path("country_id") countryId: Int): Call<Data<List<Region>>>

    @GET("regions/cities/{province_id}")
    fun getCities(@Path("province_id") provinceId: Int): Call<Data<List<Region>>>

    @GET("regions/districts/{city_id}")
    fun getDistricts(@Path("city_id") cityId: Int): Call<Data<List<Region>>>

    @GET("timezones")
    fun getTimeZones(): Call<Data<List<String>>>

    @GET("bundles")
    fun getBundleClasses(): Call<Data<Data<List<Course>>>>

    @GET("bundles/{bundle_id}")
    fun getBundleDetails(@Path("bundle_id") bundleId: Int): Call<Data<Data<Course>>>

    @GET("panel/webinars/{course_id}/forums")
    fun getCourseForum(@Path("course_id") courseId: Int): Call<Data<Forums>>

    @GET("panel/webinars/forums/{id}/answers")
    fun getForumQuestionAnswers(@Path("id") id: Int): Call<Data<Data<List<ForumItemAnswer>>>>

    @GET("panel/webinars/{course_id}/forums")
    fun searchInCourseForum(
        @Path("course_id") courseId: Int,
        @Query("search") s: String
    ): Call<Data<Forums>>

    @GET("panel/webinars/{course_id}/noticeboards")
    fun getNotices(@Path("course_id") courseId: Int): Call<Data<List<Notice>>>

    @GET("panel/webinars/certificates")
    fun getCompletionCertificates(): Call<Data<Data<List<CompletionCert>>>>

    @GET("bundles/{bundle_id}/webinars")
    fun getCoursesForBundle(@Path("bundle_id") id: Int): Call<Data<Data<List<Course>>>>

    @GET("panel/my_assignments")
    fun getMyAssignments(): Call<Data<Data<List<Assignment>>>>

    @POST("panel/webinars/forums/{id}/pin")
    fun pinForumItem(@Path("id") id: Int, @Body follow: Follow): Call<BaseResponse>

    @POST("panel/webinars/forums/answers/{id}/pin")
    fun pinForumItemAnswer(@Path("id") id: Int, @Body follow: Follow): Call<BaseResponse>

    @POST("panel/webinars/forums/answers/{id}/resolve")
    fun markAnswerAsResolved(@Path("id") id: Int, @Body follow: Follow): Call<BaseResponse>

    @Multipart
    @POST("panel/webinars/{course_id}/forums")
    fun postForumQuestion(
        @Path("course_id") courseId: Int,
        @Part title: MultipartBody.Part,
        @Part desc: MultipartBody.Part,
        @Part attachment: MultipartBody.Part?
    ): Call<BaseResponse>

    @Multipart
    @POST("panel/webinars/forums/{forum_id}?_method=put")
    fun editForumQuestion(
        @Path("forum_id") forumId: Int,
        @Part title: MultipartBody.Part,
        @Part desc: MultipartBody.Part,
        @Part attachment: MultipartBody.Part
    ): Call<BaseResponse>

    @POST("panel/webinars/forums/{forum_id}?_method=put")
    fun editForumQuestion(
        @Path("forum_id") forumId: Int,
        @Body forum: ForumItem,
    ): Call<BaseResponse>

    @POST("panel/webinars/forums/{id}/answers")
    fun replyToForumQuestion(@Path("id") id: Int, @Body reply: Reply): Call<BaseResponse>

    @PUT("panel/webinars/forums/answers/{id}")
    fun editReplyToForumQuestion(@Path("id") id: Int, @Body reply: Reply): Call<BaseResponse>

    @GET("courses/{course_id}/content")
    fun getCourseContent(@Path("course_id") courseId: Int): Call<Data<List<Chapter>>>

    @GET("courses/{course_id}/quizzes")
    fun getCourseCertificates(@Path("course_id") courseId: Int): Call<Data<List<Quiz>>>

    @GET
    fun getSessionChapterItemDetails(@Url url: String): Call<Data<ChapterSessionItem>>

    @GET
    fun getFileChapterItemDetails(@Url url: String): Call<Data<ChapterFileItem>>

    @Multipart
    @POST("panel/assignments/{assignment_id}/messages")
    fun saveAssignmentConversation(
        @Path("assignment_id") courseId: Int,
        @Part title: MultipartBody.Part,
        @Part desc: MultipartBody.Part,
        @Part attachment: MultipartBody.Part?,
        @Part studentId: MultipartBody.Part?
    ): Call<BaseResponse>

    @GET("panel/assignments/{assignment_id}/messages")
    fun getAssignmentConversations(
        @Path("assignment_id") assignmentId: Int,
        @Query("student_id") studentId: Int?
    ): Call<Data<List<Conversation>>>

    @POST("instructor/assignments/histories/{assignment_id}/rate")
    fun rateAssignment(
        @Path("assignment_id") assignmentId: Int,
        @Body grade: Grade
    ): Call<BaseResponse>

    @GET("instructor/assignments")
    fun getStudentAssignments(): Call<Data<StudentAssignments>>

    @GET("instructor/assignments/{assignment_id}/students")
    fun getAssignmentStudents(@Path("assignment_id") assignmentId: Int): Call<Data<List<Assignment>>>

    @GET("panel/text-lessons/{text_lesson_id}/navigation")
    fun getTextLessons(@Path("text_lesson_id") textLessonId: Int): Call<Data<List<ChapterTextItem>>>

    @GET("panel/my_assignments/{assignment_id}")
    fun getAssignment(@Path("assignment_id") assignmentId: Int): Call<Data<Assignment>>

}