<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  assignUserRoles,
  createUser,
  getRoleList,
  getUserList,
  resetUserPassword,
  updateUserStatus,
} from '@/api/modules/system-user'
import { useAuthStore } from '@/store/modules/auth'
import type { RoleItem, UserItem } from '@/types/system-user'

const authStore = useAuthStore()
const loading = ref(false)
const submitLoading = ref(false)
const createDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const userList = ref<UserItem[]>([])
const roleList = ref<RoleItem[]>([])
const currentUser = ref<UserItem | null>(null)

const query = reactive({
  keyword: '',
})

const createForm = reactive({
  username: '',
  realName: '',
  password: '123456',
  status: 1,
  roleIds: [] as number[],
})

const roleForm = reactive({
  roleIds: [] as number[],
})

const passwordForm = reactive({
  newPassword: '123456',
})

const filteredUsers = computed(() => {
  const keyword = query.keyword.trim().toLowerCase()
  if (!keyword) {
    return userList.value
  }

  return userList.value.filter((item) => (
    item.username.toLowerCase().includes(keyword)
    || item.realName.toLowerCase().includes(keyword)
    || item.roleNames.join(',').toLowerCase().includes(keyword)
  ))
})

const canManageUsers = computed(() => authStore.roleCodes.includes('ADMIN'))

async function loadPageData() {
  loading.value = true
  try {
    const [userResp, roleResp] = await Promise.all([
      getUserList(),
      getRoleList(),
    ])
    userList.value = userResp.data.data
    roleList.value = roleResp.data.data
  } finally {
    loading.value = false
  }
}

function resetCreateForm() {
  createForm.username = ''
  createForm.realName = ''
  createForm.password = '123456'
  createForm.status = 1
  createForm.roleIds = []
}

function openCreateDialog() {
  resetCreateForm()
  createDialogVisible.value = true
}

function openAssignDialog(user: UserItem) {
  currentUser.value = user
  roleForm.roleIds = [...user.roleIds]
  roleDialogVisible.value = true
}

function openResetPasswordDialog(user: UserItem) {
  currentUser.value = user
  passwordForm.newPassword = '123456'
  passwordDialogVisible.value = true
}

async function handleCreateUser() {
  submitLoading.value = true
  try {
    await createUser({
      username: createForm.username.trim(),
      realName: createForm.realName.trim(),
      password: createForm.password,
      status: createForm.status,
      roleIds: createForm.roleIds,
    })
    ElMessage.success('用户已创建')
    createDialogVisible.value = false
    await loadPageData()
  } finally {
    submitLoading.value = false
  }
}

async function handleAssignRoles() {
  if (!currentUser.value) {
    return
  }

  submitLoading.value = true
  try {
    await assignUserRoles(currentUser.value.id, roleForm.roleIds)
    ElMessage.success('角色分配已更新')
    roleDialogVisible.value = false
    await loadPageData()
  } finally {
    submitLoading.value = false
  }
}

async function handleResetPassword() {
  if (!currentUser.value) {
    return
  }

  submitLoading.value = true
  try {
    await resetUserPassword(currentUser.value.id, {
      newPassword: passwordForm.newPassword,
    })
    ElMessage.success('密码已重置')
    passwordDialogVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

async function handleToggleStatus(user: UserItem) {
  const nextStatus = user.status === 1 ? 0 : 1
  const actionText = nextStatus === 1 ? '启用' : '停用'

  await ElMessageBox.confirm(`确认${actionText}用户 ${user.username} 吗？`, `${actionText}用户`, {
    type: 'warning',
  })

  await updateUserStatus(user.id, { status: nextStatus })
  ElMessage.success(`用户已${actionText}`)
  await loadPageData()
}

onMounted(() => {
  loadPageData()
})
</script>

<template>
  <div class="user-page">
    <div class="page-card">
      <div class="toolbar">
        <div>
          <div class="section-title">用户管理</div>
          <div class="section-subtitle">管理员可新增用户、分配角色、重置密码和启停账号</div>
        </div>
        <div class="toolbar-actions">
          <el-input v-model="query.keyword" placeholder="按用户名、姓名、角色搜索" clearable />
          <el-button type="primary" :disabled="!canManageUsers" @click="openCreateDialog">
            新增用户
          </el-button>
        </div>
      </div>

      <el-table :data="filteredUsers" v-loading="loading" border>
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="realName" label="姓名" width="140" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" min-width="240">
          <template #default="{ row }">
            <div class="role-list">
              <el-tag
                v-for="roleName in row.roleNames"
                :key="roleName"
                type="primary"
                effect="plain"
              >
                {{ roleName }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" min-width="180" />
        <el-table-column label="操作" min-width="260" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" link :disabled="!canManageUsers" @click="openAssignDialog(row)">
                分配角色
              </el-button>
              <el-button type="warning" link :disabled="!canManageUsers" @click="openResetPasswordDialog(row)">
                重置密码
              </el-button>
              <el-button type="danger" link :disabled="!canManageUsers" @click="handleToggleStatus(row)">
                {{ row.status === 1 ? '停用' : '启用' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="createDialogVisible" title="新增用户" width="520px" destroy-on-close>
      <el-form label-width="88px">
        <el-form-item label="用户名">
          <el-input v-model="createForm.username" placeholder="例如：pharmacist01" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="createForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="createForm.password" show-password />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="createForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="createForm.roleIds" class="role-checkbox-group">
            <el-checkbox v-for="role in roleList" :key="role.id" :value="role.id">
              {{ role.roleName }} ({{ role.roleCode }})
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleCreateUser">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="520px" destroy-on-close>
      <div v-if="currentUser" class="dialog-panel">
        <div class="dialog-user">{{ currentUser.realName }} / {{ currentUser.username }}</div>
        <el-checkbox-group v-model="roleForm.roleIds" class="role-checkbox-group">
          <el-checkbox v-for="role in roleList" :key="role.id" :value="role.id">
            {{ role.roleName }} ({{ role.roleCode }})
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAssignRoles">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="passwordDialogVisible" title="重置密码" width="440px" destroy-on-close>
      <div v-if="currentUser" class="dialog-panel">
        <div class="dialog-user">{{ currentUser.realName }} / {{ currentUser.username }}</div>
        <el-input v-model="passwordForm.newPassword" show-password placeholder="请输入新密码" />
      </div>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleResetPassword">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-page {
  display: grid;
  gap: 16px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  min-width: 360px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #162033;
}

.section-subtitle {
  margin-top: 6px;
  color: #64748b;
}

.role-list,
.table-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.dialog-panel {
  display: grid;
  gap: 16px;
}

.dialog-user {
  font-size: 15px;
  font-weight: 700;
  color: #162033;
}

.role-checkbox-group {
  display: grid;
  gap: 10px;
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
  }

  .toolbar-actions {
    min-width: 100%;
    width: 100%;
    flex-direction: column;
  }
}
</style>
