from rest_framework.routers import DefaultRouter
from .views import WasteViewSet

router = DefaultRouter()
router.register('waste', WasteViewSet)

urlpatterns = router.urls
