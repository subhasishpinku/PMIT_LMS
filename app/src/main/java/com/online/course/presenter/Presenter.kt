package com.online.course.presenter

import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.OnDownloadProgressListener
import com.online.course.model.*
import com.online.course.ui.frag.UsersOrganizationsFrag
import java.io.File

class Presenter {

    interface ThirdPartyPresenter {
        fun facebookSignInUp(thirdPartyLogin: ThirdPartyLogin)
        fun googleSignInUp(thirdPartyLogin: ThirdPartyLogin)
    }

    interface SignInPresenter : ThirdPartyPresenter {
        fun login(login: Login)
    }

    interface SignUpPresenter : ThirdPartyPresenter {
        fun signUp(signUp: EmailSignUp)
        fun signUp(signUp: MobileSignUp)
    }

    interface SplashScreenPresenter {
        fun getAppConfig()
    }

    interface VerifyAccountPresenter {
        fun signUp(signUp: EmailSignUp)
        fun signUp(signUp: MobileSignUp)
        fun verifyAccount(accountVerification: AccountVerification)
    }

    interface UserRegistrationPresenter {
        fun register(user: User)
    }

    interface HomePresenter {
        fun getFeaturedCourses()
        fun getNewestCourses(map: HashMap<String, String>)
        fun getBestRatedCourses(map: HashMap<String, String>)
        fun getBestSellingCourses(map: HashMap<String, String>)
        fun getDiscountedCourses(map: HashMap<String, String>)
        fun getFreeCourses(map: HashMap<String, String>)
        fun getBundles()
    }

    interface SearchResultPresenter {
        fun search(s: String)
    }

    interface BaseCategoriesPresenter {
        fun getCategories()
    }

    interface CategoriesPresenter : BaseCategoriesPresenter {
        fun getTrendingCategories()
    }

    interface CategoryPresenter {
        fun getCatFiltersAndCourses(categoryId: Int)
        fun getCatFeaturedCourses(categoryId: Int)
        fun getCatFeaturedCourses(
            categoryId: Int,
            selectedOptions: ArrayList<KeyValuePair>?,
            selectedFilters: ArrayList<KeyValuePair>?
        )
    }

    interface BlogPresenter {
        fun getBlogs()
        fun getBlogs(catId: Int)
    }

    interface ReportReplyCommentPresenter {
        fun comment(comment: Comment)
        fun reply(comment: Comment)
        fun reportComment(comment: Comment)
        fun editComment(comment: Comment)
        fun reportCourse(comment: Comment)
        fun getReasons()
    }

    interface ProvidersPresenter {
        fun getProviders(type: UsersOrganizationsFrag.ProviderType, map: List<KeyValuePair>?)
    }

    interface ProvidersFiltersPresenter {
        fun getCategories()
    }

    interface CommentsPresenter {
        fun getComments(callback: ItemCallback<Comments>)
    }

    interface CommentDetailsPresenter {
        fun removeComment(commentId: Int)
    }

    interface MeetingsPresenter {
        fun getMeetings()
    }

    interface SettingsGeneralPresenter {
        fun changeProfileSettings(changeSettings: UserChangeSettings)
    }

    interface SettingsSecurityPresenter {
        fun changePassword(changePassword: ChangePassword)
    }

    interface MeetingDetailsMorePresenter {
        fun finishMeeting(meetingId: Int)
    }

    interface MeetingJoinDetailsPresenter {
        fun createJoin(reserveMeeting: ReserveMeeting)
    }

    interface CertificatesPresenter {
        fun getAchievementCertificates()
        fun getClassCertificates()
        fun getCompletionCertificates()
    }

    interface ProgressiveLoadingPresenter {
        fun downloadFile(
            fileDir: String?,
            fileUrl: String,
            downloadListener: OnDownloadProgressListener,
            toDownloads: Boolean,
            fileNameFromHeader: Boolean,
            defaultFileName: String
        )
    }

    interface ClassesPresenter {
        fun getCourses()
    }

    interface SearchPresenter {
        fun getBestRatedCourses()
    }

    interface ProfilePresenter {
        fun getUserProfile(userId: Int)
    }

    interface ProfileAboutPresenter {
        fun follow(userId: Int, follow: Follow)
    }

    interface ReserveMeetingDialogPresenter {
        fun getAvailableMeetingTimes(userId: Int, date: String)
    }

    interface FinalizeReserveMeetingPresenter {
        fun reserveMeeting(reserveMeeting: ReserveTimeMeeting)
    }

    interface NotifPresenter {
        fun getNotifs()
    }

    interface SupportPresenter {
        fun getTickets()
        fun getClassSupport()
        fun getMyClassSupport()
    }

    interface NewTicketPresenter {
        fun getDepartments()
        fun addTicket(ticket: Ticket, file: File?)
        fun addTicketChat(conversation: Conversation, file: File?)
        fun getCourses()
    }

    interface TicketConversationPresenter {
        fun closeTicket(ticketId: Int)
    }

    interface QuzziesPresenter {
        fun getMyResults()
        fun getNotParticipated()
        fun getQuizList()
        fun getStudentResults()
    }

    interface QuizOverviewPresenter {
        fun startQuiz(id: Int)
    }

    interface QuizPresenter {
        fun storeResult(quizId: Int, answer: QuizAnswer)
    }

    interface QuizResultInfoPresenter {
        fun startQuiz(id: Int)
        fun getQuizResult(quizId: Int)
    }

    interface QuizReviewPresenter {
        fun storeReviewResult(resultId: Int, review: List<QuizAnswerItem>)
    }

    interface FinancialSummaryPresenter {
        fun getSummary()
        fun chargeAccount(paymentRequest: PaymentRequest)
    }

    interface CommonApiPresenter {
        fun getQuickInfo(callback: ItemCallback<QuickInfo>)
        fun addToCart(addToCart: AddToCart, callback: ItemCallback<BaseResponse>)
        fun getUserInfo(userId: Int, callback: ItemCallback<UserProfile>)
        fun getCourseDetails(courseId: Int, callback: ItemCallback<Course>)
        fun getBundleDetails(bundleId: Int, callback: ItemCallback<Course>)
        fun getCourseContent(courseId: Int, callback: ItemCallback<List<Chapter>>)
        fun getCourseCerts(courseId: Int, callback: ItemCallback<List<Quiz>>)
        fun getFileSize(url: String, sizeCallback: ItemCallback<Long>)
    }

    interface BanksInfoPresenter {
        fun getBanksInfo()
    }

    interface FinancialOfflinePaymentsPresenter {
        fun getOfflinePayments()
    }

    interface FinancialPayoutPresenter {
        fun getPayouts()
    }

    interface PayoutRequestPresenter {
        fun requestPayout()
    }

    interface FinancialSalesPresenter {
        fun getSales()
    }

    interface OfflinePaymentDialogPresenter {
        fun addOfflinePayment(offlinePayment: OfflinePayment)
    }

    interface FavoritesPresenter {
        fun getFavorites()
        fun removeFromFavorite(addToFav: AddToFav, adapterPosition: Int)
    }

    interface MyClassesPresenter {
        fun getMyClasses()
        fun getPurchased()
        fun getOrganizations()
    }

    interface SubscriptionPresenter {
        fun getSubscriptions()
        fun checkoutSubscription(subscriptionItem: SubscriptionItem)
    }

    interface CartPresenter {
        fun getCart()
        fun removeFromCart(cartItemId: Int, position: Int)
        fun checkout(coupon: Coupon?)
    }

    interface CouponPresenter {
        fun validateCoupon(coupon: Coupon)
    }

    interface NewMessagePresenter {
        fun addMessage(userId: Int, message: Message)
    }

    interface CourseDetailsMorePresenter {
        fun addToFavorite(addToFav: AddToFav)
    }

    interface CourseReviewPresenter {
        fun addReview(review: Review)

    }

    interface CourseDetailsPresenter {
        fun subscribe(addToCart: AddToCart)
        fun addCourseToUserCourse(courseId: Int)
    }

    interface NotifDialogPresenter {
        fun setStatusToSeen(notifId: Int)
    }

    interface CertificateDetailsPresenter {
        fun getStudents()
    }

    interface ChargeAccountPaymentPresenter {
        fun requestPayment(paymentRequest: PaymentRequest)
        fun chargeAccount(paymentRequest: PaymentRequest)
        fun requestPaymentFromCharge(paymentRequest: PaymentRequest)
    }

    interface ChargeAccountPresenter {
        fun chargeAccount(paymentRequest: PaymentRequest)
    }

    interface SettingsPresenter {
        fun uploadPhoto(path: String)
    }

    interface SettingsFinancialPresenter {
        fun uploadFinancialSettings(financialSettings: FinancialSettings)
        fun getAccountTypes()
        fun uploadFinancialSettingsFiles(identityFile: File, certFile: File)
    }

    interface CourseChapterItemPresenter {
        fun getSessionItemDetails(url: String, callback: ItemCallback<ChapterSessionItem>)
        fun getFileItemDetails(url: String, callback: ItemCallback<ChapterFileItem>)
        fun changeItemStatus(chapterItemMark: ChapterItemMark)
        fun downloadFile(file: ChapterFileItem, progressListener: OnDownloadProgressListener)
        fun cancelDownload(fileId: Int)
        fun getTextLessons(textLessonId: Int, callback: ItemCallback<List<ChapterTextItem>>)
    }

    interface ForgotPasswordPresenter {
        fun sendChangePasswordLink(forgetPassword: ForgetPassword)
    }

    interface BlogCategoriesPresenter {
        fun getBlogCategories()
    }

    interface LogoutPresenter {
        fun logout()
    }

    interface RewardPointsPresenter {
        fun getPoints()
    }

    interface SaasPackagePresenter {
        fun getSaasPackages()
        fun checkoutSubscription(saasPackageItem: SaasPackageItem)
    }

    interface RewardCoursesPresenter {
        fun getRewardCourses(categories: List<KeyValuePair>?, options: List<KeyValuePair>?)
    }

    interface PricingPlansPresenter {
        fun purchaseWithPoints(course: Course)
    }

    interface SettingsLocalizationPresenter {
        fun getTimeZones()
        fun getCountries()
        fun getProvinces(countryId: Int)
        fun getCities(provinceId: Int)
        fun getDistricts(cityId: Int)
        fun changeProfileSettings(changeSettings: UserChangeLocalization)
    }

    interface ForumsPresenter {
        fun getForumQuestions(courseId: Int)
        fun searchInCourseForum(courseId: Int, s: String)
    }

    interface NoticesPresenter {
        fun getNotices(courseId: Int)
    }

    interface BundleCoursesPresenter {
        fun getBundleCourses(id: Int)
    }

    interface MyAssignmentsPresenter {
        fun getMyAssignments()
    }

    interface ForumOptionsPresenter {
        fun pinForumItem(id: Int)
        fun pinForumItemAnswer(id: Int)
        fun markAnswerAsResolved(id: Int)
    }

    interface ForumQuestionPresenter {
        fun sendQuestion(courseId: Int, forumItem: ForumItem, file: File?)
        fun editQuestion(forumItem: ForumItem, file: File?)
    }

    interface ReplyToCourseForumPresenter {
        fun reply(forum: ForumItem, reply: Reply)
    }

    interface ForumAnswersPresenter {
        fun getForumQuestionAnswers(forumId: Int)
    }

    interface StudentAssignmentsPresenter {
        fun getStudentAssignments()
    }

    interface AssignmentNewConversationPresenter {
        fun saveConversation(assignmentId: Int, conversation: Conversation, file: File?)
    }

    interface AssignmentConversationPresenter {
        fun getConversations(assignment: Assignment, studentId: Int?)
    }

    interface RateAssignmentPresenter {
        fun rateAssignment(assignmentId: Int, grade: Grade)
    }

    interface AssignmentOverviewPresenter {
        fun getAssignmentStudents(assignmentId: Int)
        fun getAssignment(assignmentId: Int)
    }
}