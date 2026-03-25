<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { assignUserRoles, getRoleList, getUserList } from '@/api/modules/system-user'
import { useAuthStore } from '@/store/modules/auth'
import type { RoleItem, UserItem } from '@/types/system-user'

const authStore = useAuthStore()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const userList = ref<UserItem[]>([])
const roleList = ref<RoleItem[]>([])
const currentUser = ref<UserItem | null>(null)

const query = reactive({
  keyword: '',
})

const form = reactive({
  roleIds: [] as number[],
})

const filteredUsers = computed(() => {
  const keyword = query.keyword.trim().toLowerCase()
  if (!keyword) {
    return userList.value
  }

  return userList.value.filter((item) => {
    return item.username.toLowerCase().includes(keyword)
      || item.realName.toLowerCase().includes(keyword)
      || item.roleNames.join(',').toLowerCase().includes(keyword)
  })
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

function openAssignDialog(user: UserItem) {
  currentUser.value = user
  form.roleIds = [...user.roleIds]
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!currentUser.value) {
    return
  }

  submitLoading.value = true
  try {
    await assignUserRoles(currentUser.value.id, form.roleIds)
    ElMessage.success('角色分配已更新')
    dialogVisible.value = false
    await loadPageData()
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadPageData()
})
</script>

<template>
  <div class="user-page">
    <div class="page-card">
      <div class="toolbar">
        <div class="section-title">用户角色管理</div>
        <div class="toolbar-actions">
          <el-input v-model="query.keyword" placeholder="按用户名、姓名、角色搜索" clearable />
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
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              :disabled="!canManageUsers"
              @click="openAssignDialog(row)"
            >
              分配角色
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      v-model="dialogVisible"
      title="分配角色"
      width="520px"
      destroy-on-close
    >
      <div v-if="currentUser" class="assign-dialog">
        <div class="dialog-user">
          {{ currentUser.realName }} / {{ currentUser.username }}
        </div>
        <el-checkbox-group v-model="form.roleIds" class="role-checkbox-group">
          <el-checkbox
            v-for="role in roleList"
            :key="role.id"
            :value="role.id"
          >
            {{ role.roleName }} ({{ role.roleCode }})
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
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
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  min-width: 320px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #162033;
}

.role-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.assign-dialog {
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
</style>
